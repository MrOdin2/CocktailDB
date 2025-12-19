import { Injectable } from '@angular/core';

export interface FuzzyMatch<T> {
  item: T;
  score: number;
  matchedText?: string;
  matchIndex?: number;
}

@Injectable({
  providedIn: 'root'
})
export class FuzzySearchService {
  // Maximum ratio of distance to string length for fuzzy matching
  private readonly MAX_DISTANCE_RATIO = 0.3;

  /**
   * Calculates the Levenshtein distance between two strings.
   * This represents the minimum number of single-character edits needed to transform one string into another.
   */
  private levenshteinDistance(str1: string, str2: string): number {
    const len1 = str1.length;
    const len2 = str2.length;
    const matrix: number[][] = [];

    // Initialize matrix
    for (let i = 0; i <= len1; i++) {
      matrix[i] = [i];
    }
    for (let j = 0; j <= len2; j++) {
      matrix[0][j] = j;
    }

    // Fill matrix
    for (let i = 1; i <= len1; i++) {
      for (let j = 1; j <= len2; j++) {
        const cost = str1[i - 1] === str2[j - 1] ? 0 : 1;
        matrix[i][j] = Math.min(
          matrix[i - 1][j] + 1,      // deletion
          matrix[i][j - 1] + 1,      // insertion
          matrix[i - 1][j - 1] + cost // substitution
        );
      }
    }

    return matrix[len1][len2];
  }

  /**
   * Calculates a fuzzy match score between a query and a target string.
   * Returns a score between 0 (no match) and 1 (perfect match).
   * 
   * @param query - The search query
   * @param target - The string to match against
   * @param threshold - Maximum allowed distance (default: 2 for typo tolerance)
   * @returns Score between 0 and 1, or 0 if no match
   */
  private calculateScore(query: string, target: string, threshold: number = 2): number {
    const queryLower = query.toLowerCase().trim();
    const targetLower = target.toLowerCase().trim();

    // Exact match gets highest score
    if (targetLower === queryLower) {
      return 1.0;
    }

    // Check if target contains query (substring match)
    if (targetLower.includes(queryLower)) {
      return 0.9;
    }

    // Check if query contains target (partial match)
    if (queryLower.includes(targetLower)) {
      return 0.85;
    }

    // Check for fuzzy match with Levenshtein distance
    const distance = this.levenshteinDistance(queryLower, targetLower);
    const maxLength = Math.max(queryLower.length, targetLower.length);
    
    // Only consider matches within threshold
    if (distance > threshold && distance > maxLength * this.MAX_DISTANCE_RATIO) {
      return 0;
    }

    // Calculate score based on similarity
    const similarity = 1 - (distance / maxLength);
    return Math.max(0, similarity * 0.8); // Scale down fuzzy matches
  }

  /**
   * Searches for the best fuzzy match in a target string for a query.
   * Handles partial word matching and substring searches.
   * 
   * @param query - The search query
   * @param target - The string to search in
   * @param threshold - Maximum allowed distance for fuzzy matching
   * @returns Score and matched substring information
   */
  private findBestMatch(query: string, target: string, threshold: number = 2): { score: number; matchedText?: string; matchIndex?: number } {
    const queryLower = query.toLowerCase().trim();
    const targetLower = target.toLowerCase().trim();

    // Try exact match first
    let bestScore = this.calculateScore(query, target, threshold);
    let bestMatch = { score: bestScore, matchedText: target, matchIndex: 0 };

    if (bestScore >= 0.9) {
      return bestMatch;
    }

    // Try matching against individual words in target
    const words = targetLower.split(/\s+/);
    const queryWords = queryLower.split(/\s+/);
    
    // If query has multiple words, check if all words match (in any order)
    if (queryWords.length > 1 && words.length > 1) {
      let allWordsMatch = true;
      let matchedWordsScore = 0;
      
      for (const qWord of queryWords) {
        let foundMatch = false;
        for (const tWord of words) {
          const wordScore = this.calculateScore(qWord, tWord, threshold);
          if (wordScore >= 0.7) {
            foundMatch = true;
            matchedWordsScore += wordScore;
            break;
          }
        }
        if (!foundMatch) {
          allWordsMatch = false;
          break;
        }
      }
      
      if (allWordsMatch) {
        // Multi-word match: average score but boost for matching all words
        const avgScore = matchedWordsScore / queryWords.length;
        const multiWordBonus = 0.05; // Small boost for matching all query words
        const multiWordScore = Math.min(0.98, avgScore + multiWordBonus);
        
        if (multiWordScore > bestScore) {
          bestScore = multiWordScore;
          bestMatch = {
            score: multiWordScore,
            matchedText: target,
            matchIndex: 0
          };
        }
      }
    }
    
    // Try matching individual words
    let searchIndex = 0;
    for (let i = 0; i < words.length; i++) {
      const wordScore = this.calculateScore(queryLower, words[i], threshold);
      // Find the actual position of this word in the original string
      // Note: This uses indexOf which may find duplicate words, but for typical
      // cocktail/ingredient names this is rare and acceptable for the MVP
      const wordIndex = targetLower.indexOf(words[i], searchIndex);
      
      // Penalize single-word matches compared to full-string matches
      // Single word match gets 0.85x of its score to ensure full matches rank higher
      const adjustedScore = wordScore * 0.85;
      
      if (adjustedScore > bestScore) {
        bestScore = adjustedScore;
        bestMatch = {
          score: adjustedScore,
          matchedText: words[i],
          matchIndex: wordIndex >= 0 ? wordIndex : 0
        };
      }
      // Update search position to after this word
      if (wordIndex >= 0) {
        searchIndex = wordIndex + words[i].length;
      }
    }

    return bestMatch;
  }

  /**
   * Performs a fuzzy search on an array of items.
   * 
   * @param query - The search query
   * @param items - Array of items to search
   * @param getSearchText - Function to extract searchable text from each item
   * @param threshold - Maximum allowed distance for fuzzy matching (default: 2)
   * @param minScore - Minimum score to include in results (default: 0.3)
   * @returns Array of matching items with scores, sorted by score
   */
  search<T>(
    query: string,
    items: T[],
    getSearchText: (item: T) => string | string[],
    threshold: number = 2,
    minScore: number = 0.3
  ): FuzzyMatch<T>[] {
    if (!query || query.trim().length === 0) {
      return items.map(item => ({ item, score: 1.0 }));
    }

    const results: FuzzyMatch<T>[] = [];

    for (const item of items) {
      const searchTexts = getSearchText(item);
      const textsArray = Array.isArray(searchTexts) ? searchTexts : [searchTexts];
      
      let bestMatch: { score: number; matchedText?: string; matchIndex?: number } = { score: 0, matchedText: undefined as string | undefined, matchIndex: undefined as number | undefined };

      // Find best match across all searchable fields
      // First field gets priority (typically the primary field like name)
      for (let fieldIndex = 0; fieldIndex < textsArray.length; fieldIndex++) {
        const text = textsArray[fieldIndex];
        if (!text) continue;
        
        const match = this.findBestMatch(query, text, threshold);
        
        // Apply field priority weighting
        // First field (index 0) gets full score, subsequent fields get reduced weight
        let weightedScore = match.score;
        if (fieldIndex > 0) {
          // Reduce score for secondary fields to prioritize primary field matches
          weightedScore = match.score * 0.95;
        }
        
        if (weightedScore > bestMatch.score) {
          bestMatch = {
            score: weightedScore,
            matchedText: match.matchedText,
            matchIndex: match.matchIndex
          };
        }
      }

      // Include items that meet minimum score threshold
      if (bestMatch.score >= minScore) {
        results.push({
          item,
          score: bestMatch.score,
          matchedText: bestMatch.matchedText,
          matchIndex: bestMatch.matchIndex
        });
      }
    }

    // Sort by score (highest first)
    return results.sort((a, b) => b.score - a.score);
  }

  /**
   * Simple fuzzy match check - returns true if query fuzzy-matches the target.
   * Useful for simple filtering without needing full search results.
   * 
   * @param query - The search query
   * @param target - The string to match against
   * @param threshold - Maximum allowed distance (default: 2)
   * @param minScore - Minimum score to consider a match (default: 0.3)
   * @returns True if there's a fuzzy match
   */
  matches(query: string, target: string, threshold: number = 2, minScore: number = 0.3): boolean {
    if (!query || query.trim().length === 0) {
      return true;
    }
    if (!target) {
      return false;
    }
    
    const match = this.findBestMatch(query, target, threshold);
    return match.score >= minScore;
  }
}
