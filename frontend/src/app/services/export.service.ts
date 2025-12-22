import { Injectable } from '@angular/core';
import { Cocktail, Ingredient } from '../models/models';
import { MeasureService } from './measure.service';

export enum ExportFormat {
  HTML = 'html',
  MARKDOWN = 'md',
  PDF = 'pdf',
  CSV = 'csv'
}

export enum ExportType {
  MENU = 'menu',
  CHEATSHEET = 'cheatsheet'
}

@Injectable({
  providedIn: 'root'
})
export class ExportService {

  constructor(private measureService: MeasureService) {}

  /**
   * Export cocktails based on type and format
   */
  exportCocktails(
    cocktails: Cocktail[],
    ingredients: Ingredient[],
    type: ExportType,
    format: ExportFormat,
    groupBy: 'spirit' | 'tags' = 'spirit',
    selectedTags?: string[]
  ): void {
    let content: string = "none";
    let filename: string= "none";
    let mimeType: string= "none";

    const timestamp = new Date().toISOString().split('T')[0];
    const baseFilename = `${type}-${timestamp}`;

    switch (format) {
      case ExportFormat.HTML:
        content = type === ExportType.MENU 
          ? this.generateMenuHTML(cocktails, ingredients, groupBy, selectedTags)
          : this.generateCheatSheetHTML(cocktails, ingredients);
        filename = `${baseFilename}.html`;
        mimeType = 'text/html';
        break;
      case ExportFormat.MARKDOWN:
        content = type === ExportType.MENU
          ? this.generateMenuMarkdown(cocktails, ingredients, groupBy, selectedTags)
          : this.generateCheatSheetMarkdown(cocktails, ingredients);
        filename = `${baseFilename}.md`;
        mimeType = 'text/markdown';
        break;
      case ExportFormat.PDF:
        // For PDF, we'll generate HTML and use the browser's print-to-PDF functionality
        content = type === ExportType.MENU
          ? this.generateMenuHTML(cocktails, ingredients, groupBy, selectedTags)
          : this.generateCheatSheetHTML(cocktails, ingredients);
        this.printToPDF(content, baseFilename);
        return;
    }

    this.downloadFile(content, filename, mimeType);
  }

  /**
   * Generate Menu HTML
   */
  private generateMenuHTML(cocktails: Cocktail[], ingredients: Ingredient[], groupBy: 'spirit' | 'tags', selectedTags?: string[]): string {
    const groups = this.groupCocktails(cocktails, ingredients, groupBy, selectedTags);
    
    let html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cocktail Menu</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
      padding: 0;
      background: #f5f5f5;
    }
    h1 {
      text-align: center;
      color: #333;
      border-bottom: 2px solid #333;
      padding-bottom: 8px;
      margin-bottom: 15px;
      font-size: 1.5em;
    }
    h2 {
      color: #555;
      margin-top: 20px;
      border-bottom: 1px solid #999;
      padding-bottom: 3px;
      font-size: 1.1em;
      column-span: all;
    }
    .cocktail-container {
      column-count: 2;
      column-gap: 20px;
    }
    .cocktail {
      background: white;
      padding: 10px;
      margin: 0 0 10px 0;
      border-radius: 3px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      break-inside: avoid;
      page-break-inside: avoid;
    }
    .cocktail-name {
      font-size: 0.95em;
      font-weight: bold;
      color: #222;
      margin-bottom: 5px;
    }
    .cocktail-tags {
      font-size: 0.75em;
      color: #666;
      font-style: italic;
      margin-bottom: 5px;
    }
    .ingredients {
      color: #666;
      line-height: 1.4;
      font-size: 0.85em;
      margin: 0;
      padding-left: 20px;
    }
    .ingredients li {
      margin: 2px 0;
    }
    @media print {
      body {
        background: white;
        margin: 10px;
      }
      .cocktail {
        box-shadow: none;
        border: 1px solid #ddd;
      }
    }
  </style>
</head>
<body>
  <h1>Cocktail Menu</h1>
`;

    for (const [groupName, groupCocktails] of Object.entries(groups)) {
      html += `  <h2>${groupName}</h2>\n`;
      html += `  <div class="cocktail-container">\n`;
      for (const cocktail of groupCocktails) {
        html += `    <div class="cocktail">
      <div class="cocktail-name">${this.escapeHtml(cocktail.name)}</div>
`;
        // Show tags if grouping by tags
        if (groupBy === 'tags' && cocktail.tags && cocktail.tags.length > 0) {
          html += `      <div class="cocktail-tags">${cocktail.tags.map(t => this.escapeHtml(t)).join(', ')}</div>\n`;
        }
        html += `      <ul class="ingredients">
`;
        for (const ing of cocktail.ingredients) {
          const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
          const measure = this.formatMeasure(ing.measureMl);
          html += `        <li>${this.escapeHtml(ingredientName)} - ${this.escapeHtml(measure)}</li>\n`;
        }
        html += `      </ul>
    </div>
`;
      }
      html += `  </div>\n`;
    }

    html += `</body>
</html>`;
    return html;
  }

  /**
   * Generate CheatSheet HTML
   */
  private generateCheatSheetHTML(cocktails: Cocktail[], ingredients: Ingredient[]): string {
    let html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cocktail Recipes CheatSheet</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 20px;
      padding: 0;
      background: #f5f5f5;
    }
    h1 {
      text-align: center;
      color: #333;
      border-bottom: 2px solid #333;
      padding-bottom: 8px;
      margin-bottom: 15px;
      font-size: 1.5em;
    }
    .cocktail-container {
      column-count: 2;
      column-gap: 20px;
    }
    .cocktail {
      background: white;
      padding: 12px;
      margin: 0 0 12px 0;
      border-radius: 3px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      break-inside: avoid;
      page-break-inside: avoid;
    }
    .cocktail-name {
      font-size: 1em;
      font-weight: bold;
      color: #222;
      margin-bottom: 6px;
    }
    .section-title {
      font-weight: bold;
      color: #555;
      margin-top: 6px;
      margin-bottom: 3px;
      font-size: 0.85em;
    }
    .ingredients, .steps {
      color: #666;
      line-height: 1.4;
      font-size: 0.8em;
      margin: 0;
      padding-left: 18px;
    }
    .ingredients li, .steps li {
      margin: 2px 0;
    }
    .notes {
      color: #777;
      font-style: italic;
      margin-top: 6px;
      padding: 6px;
      background: #f9f9f9;
      border-left: 2px solid #ddd;
      font-size: 0.75em;
    }
    @media print {
      body {
        background: white;
        margin: 10px;
      }
      .cocktail {
        box-shadow: none;
        border: 1px solid #ddd;
      }
    }
  </style>
</head>
<body>
  <h1>Cocktail Recipes CheatSheet</h1>
  <div class="cocktail-container">
`;

    for (const cocktail of cocktails) {
      html += `    <div class="cocktail">
      <div class="cocktail-name">${this.escapeHtml(cocktail.name)}</div>
      
      <div class="section-title">Ingredients:</div>
      <ul class="ingredients">
`;
      for (const ing of cocktail.ingredients) {
        const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
        const measure = this.formatMeasure(ing.measureMl);
        html += `        <li>${this.escapeHtml(ingredientName)} - ${this.escapeHtml(measure)}</li>\n`;
      }
      html += `      </ul>
`;

      if (cocktail.steps && cocktail.steps.length > 0) {
        html += `      
      <div class="section-title">Instructions:</div>
      <ol class="steps">
`;
        for (const step of cocktail.steps) {
          html += `        <li>${this.escapeHtml(step)}</li>\n`;
        }
        html += `      </ol>
`;
      }

      if (cocktail.notes) {
        html += `      
      <div class="notes">
        <strong>Notes:</strong> ${this.escapeHtml(cocktail.notes)}
      </div>
`;
      }

      html += `    </div>
`;
    }

    html += `  </div>
</body>
</html>`;
    return html;
  }

  /**
   * Generate Menu Markdown
   */
  private generateMenuMarkdown(cocktails: Cocktail[], ingredients: Ingredient[], groupBy: 'spirit' | 'tags', selectedTags?: string[]): string {
    const groups = this.groupCocktails(cocktails, ingredients, groupBy, selectedTags);
    
    let markdown = `# Cocktail Menu\n\n`;

    for (const [groupName, groupCocktails] of Object.entries(groups)) {
      markdown += `## ${groupName}\n\n`;
      for (const cocktail of groupCocktails) {
        markdown += `### ${cocktail.name}\n\n`;
        for (const ing of cocktail.ingredients) {
          const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
          const measure = this.formatMeasure(ing.measureMl);
          markdown += `- ${ingredientName} - ${measure}\n`;
        }
        markdown += `\n`;
      }
    }

    return markdown;
  }

  /**
   * Generate CheatSheet Markdown
   */
  private generateCheatSheetMarkdown(cocktails: Cocktail[], ingredients: Ingredient[]): string {
    let markdown = `# Cocktail Recipes CheatSheet\n\n`;

    for (const cocktail of cocktails) {
      markdown += `## ${cocktail.name}\n\n`;
      
      markdown += `### Ingredients\n\n`;
      for (const ing of cocktail.ingredients) {
        const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
        const measure = this.formatMeasure(ing.measureMl);
        markdown += `- ${ingredientName} - ${measure}\n`;
      }
      markdown += `\n`;

      if (cocktail.steps && cocktail.steps.length > 0) {
        markdown += `### Instructions\n\n`;
        cocktail.steps.forEach((step, index) => {
          markdown += `${index + 1}. ${step}\n`;
        });
        markdown += `\n`;
      }

      if (cocktail.notes) {
        markdown += `### Notes\n\n`;
        markdown += `${cocktail.notes}\n\n`;
      }

      markdown += `---\n\n`;
    }

    return markdown;
  }

  /**
   * Group cocktails by spirit or tags
   */
  private groupCocktails(
    cocktails: Cocktail[], 
    ingredients: Ingredient[], 
    groupBy: 'spirit' | 'tags',
    selectedTags?: string[]
  ): { [key: string]: Cocktail[] } {
    const groups: { [key: string]: Cocktail[] } = {};

    if (groupBy === 'spirit') {
      for (const cocktail of cocktails) {
        let spirit = 'Other';
        
        // Find the first spirit in the ingredients
        for (const ing of cocktail.ingredients) {
          const ingredient = ingredients.find(i => i.id === ing.ingredientId);
          if (ingredient && ingredient.type === 'SPIRIT') {
            spirit = ingredient.name;
            break;
          }
        }
        
        if (!groups[spirit]) {
          groups[spirit] = [];
        }
        groups[spirit].push(cocktail);
      }
    } else {
      // Group by tags - use the first matching selected tag or 'Untagged' to avoid duplicates
      for (const cocktail of cocktails) {
        let groupKey = 'Untagged';
        if (cocktail.tags && cocktail.tags.length > 0) {
          if (selectedTags && selectedTags.length > 0) {
            // Find the first tag from selectedTags that exists in cocktail.tags
            for (const selectedTag of selectedTags) {
              if (cocktail.tags.includes(selectedTag)) {
                groupKey = selectedTag;
                break;
              }
            }
          } else {
            // If no selected tags, use alphabetically first tag
            const sortedTags = [...cocktail.tags].sort();
            groupKey = sortedTags[0];
          }
        }
        
        if (!groups[groupKey]) {
          groups[groupKey] = [];
        }
        groups[groupKey].push(cocktail);
      }
    }

    return groups;
  }

  /**
   * Get ingredient name by ID
   */
  private getIngredientName(ingredientId: number, ingredients: Ingredient[]): string {
    const ingredient = ingredients.find(i => i.id === ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }
  
  /**
   * Format a measurement in ml using the user's preferred unit
   */
  private formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl);
  }

  /**
   * Escape HTML special characters
   */
  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Download file to user's browser
   */
  private downloadFile(content: string, filename: string, mimeType: string): void {
    const blob = new Blob([content], { type: mimeType });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  /**
   * Print HTML content to PDF using browser's print dialog
   * Uses DOMParser to safely parse HTML content before displaying
   */
  private printToPDF(content: string, baseFilename: string): void {
    // Create a blob URL instead of writing directly to window
    const blob = new Blob([content], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    
    // Open the blob URL in a new window
    const printWindow = window.open(url, '_blank');
    if (printWindow) {
      // Clean up the blob URL after the window loads
      printWindow.onload = () => {
        printWindow.print();
        // Clean up after a delay to allow print dialog to open
        setTimeout(() => {
          URL.revokeObjectURL(url);
        }, 100);
      };
    } else {
      // Fallback: download as HTML if popup is blocked
      this.downloadFile(content, `${baseFilename}.html`, 'text/html');
      URL.revokeObjectURL(url);
    }
  }
}
