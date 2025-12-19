import { TestBed } from '@angular/core/testing';
import { FuzzySearchService } from './fuzzy-search.service';

describe('FuzzySearchService', () => {
  let service: FuzzySearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FuzzySearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('matches', () => {
    it('should return true for exact matches', () => {
      expect(service.matches('mojito', 'Mojito')).toBe(true);
      expect(service.matches('Martini', 'martini')).toBe(true);
    });

    it('should return true for substring matches', () => {
      expect(service.matches('moj', 'Mojito')).toBe(true);
      expect(service.matches('tin', 'Martini')).toBe(true);
    });

    it('should return true for fuzzy matches with typos', () => {
      expect(service.matches('mojto', 'Mojito')).toBe(true);  // 1 typo
      expect(service.matches('martni', 'Martini')).toBe(true); // 1 typo
      expect(service.matches('vokda', 'Vodka')).toBe(true);   // 1 typo
    });

    it('should return false for non-matches', () => {
      expect(service.matches('whiskey', 'Vodka')).toBe(false);
      expect(service.matches('xyz', 'abc')).toBe(false);
    });

    it('should return true for empty query', () => {
      expect(service.matches('', 'anything')).toBe(true);
      expect(service.matches('   ', 'anything')).toBe(true);
    });

    it('should handle word boundaries in target', () => {
      expect(service.matches('old', 'Old Fashioned')).toBe(true);
      expect(service.matches('fash', 'Old Fashioned')).toBe(true);
    });
  });

  describe('search', () => {
    interface TestItem {
      name: string;
      category: string;
    }

    const testItems: TestItem[] = [
      { name: 'Mojito', category: 'Rum' },
      { name: 'Martini', category: 'Gin' },
      { name: 'Margarita', category: 'Tequila' },
      { name: 'Manhattan', category: 'Whiskey' },
      { name: 'Old Fashioned', category: 'Whiskey' }
    ];

    it('should return all items for empty query', () => {
      const results = service.search('', testItems, item => item.name);
      expect(results.length).toBe(5);
      expect(results.every(r => r.score === 1.0)).toBe(true);
    });

    it('should find exact matches with highest score', () => {
      const results = service.search('Mojito', testItems, item => item.name);
      expect(results.length).toBeGreaterThan(0);
      expect(results[0].item.name).toBe('Mojito');
      expect(results[0].score).toBe(1.0);
    });

    it('should find substring matches', () => {
      const results = service.search('Mar', testItems, item => item.name);
      expect(results.length).toBeGreaterThanOrEqual(2);
      expect(results.some(r => r.item.name === 'Martini')).toBe(true);
      expect(results.some(r => r.item.name === 'Margarita')).toBe(true);
    });

    it('should find fuzzy matches with typos', () => {
      const results = service.search('mojto', testItems, item => item.name);
      expect(results.length).toBeGreaterThan(0);
      expect(results[0].item.name).toBe('Mojito');
      expect(results[0].score).toBeGreaterThan(0.3);
    });

    it('should sort results by score', () => {
      const results = service.search('man', testItems, item => item.name);
      // Ensure results are sorted by score (descending)
      for (let i = 0; i < results.length - 1; i++) {
        expect(results[i].score).toBeGreaterThanOrEqual(results[i + 1].score);
      }
    });

    it('should search across multiple fields', () => {
      const results = service.search('Rum', testItems, item => [item.name, item.category]);
      expect(results.some(r => r.item.name === 'Mojito')).toBe(true);
    });

    it('should prioritize full multi-word matches over partial single-word matches', () => {
      const cocktails = [
        { name: 'Vodka Tonic', category: 'Vodka' },
        { name: 'Gin and Tonic', category: 'Gin' },
        { name: 'Vodka Gimlet', category: 'Vodka' }
      ];
      
      const results = service.search('Vodka Tonic', cocktails, c => [c.name, c.category]);
      
      // Vodka Tonic should be first (exact match on name)
      expect(results[0].item.name).toBe('Vodka Tonic');
      expect(results[0].score).toBeGreaterThan(0.95);
      
      // Other cocktails should score lower
      if (results.length > 1) {
        expect(results[0].score).toBeGreaterThan(results[1].score);
      }
    });

    it('should match all words in multi-word queries', () => {
      const cocktails = [
        { name: 'Vodka Tonic', category: 'Vodka' },
        { name: 'Tonic Water', category: 'Mixer' },
        { name: 'Vodka Martini', category: 'Vodka' }
      ];
      
      const results = service.search('Vodka Tonic', cocktails, c => c.name);
      
      // Vodka Tonic should match with high score (both words match)
      const vodkaTonic = results.find(r => r.item.name === 'Vodka Tonic');
      expect(vodkaTonic).toBeDefined();
      expect(vodkaTonic!.score).toBeGreaterThan(0.9);
    });

    it('should respect custom threshold', () => {
      // With threshold 0, only exact/substring matches should work
      const strictResults = service.search('mojto', testItems, item => item.name, 0);
      expect(strictResults.length).toBe(0);

      // With threshold 2, fuzzy matches should work
      const lenientResults = service.search('mojto', testItems, item => item.name, 2);
      expect(lenientResults.length).toBeGreaterThan(0);
    });

    it('should respect minimum score', () => {
      const highMinScore = service.search('xyz', testItems, item => item.name, 2, 0.8);
      expect(highMinScore.length).toBe(0);

      const lowMinScore = service.search('moj', testItems, item => item.name, 2, 0.3);
      expect(lowMinScore.length).toBeGreaterThan(0);
    });

    it('should handle empty arrays', () => {
      const results = service.search('test', [], item => item);
      expect(results.length).toBe(0);
    });

    it('should handle items with no searchable text', () => {
      const emptyItems = [{ name: '' }, { name: '' }];
      const results = service.search('test', emptyItems, item => item.name);
      expect(results.length).toBe(0);
    });
  });

  describe('performance', () => {
    it('should handle large datasets efficiently', () => {
      const largeDataset = Array.from({ length: 1000 }, (_, i) => ({
        name: `Item ${i}`,
        description: `Description for item ${i}`
      }));

      const startTime = performance.now();
      const results = service.search('Item 5', largeDataset, item => item.name);
      const endTime = performance.now();

      expect(results.length).toBeGreaterThan(0);
      expect(endTime - startTime).toBeLessThan(500); // Should complete in less than 500ms
    });
  });
});
