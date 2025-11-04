import { Injectable } from '@angular/core';
import { Cocktail, Ingredient } from '../models/models';

export enum ExportFormat {
  HTML = 'html',
  MARKDOWN = 'md',
  PDF = 'pdf'
}

export enum ExportType {
  MENU = 'menu',
  CHEATSHEET = 'cheatsheet'
}

@Injectable({
  providedIn: 'root'
})
export class ExportService {

  constructor() {}

  /**
   * Export cocktails based on type and format
   */
  exportCocktails(
    cocktails: Cocktail[],
    ingredients: Ingredient[],
    type: ExportType,
    format: ExportFormat,
    groupBy: 'spirit' | 'tags' = 'spirit'
  ): void {
    let content: string;
    let filename: string;
    let mimeType: string;

    const timestamp = new Date().toISOString().split('T')[0];
    const baseFilename = `${type}-${timestamp}`;

    switch (format) {
      case ExportFormat.HTML:
        content = type === ExportType.MENU 
          ? this.generateMenuHTML(cocktails, ingredients, groupBy)
          : this.generateCheatSheetHTML(cocktails, ingredients);
        filename = `${baseFilename}.html`;
        mimeType = 'text/html';
        break;
      case ExportFormat.MARKDOWN:
        content = type === ExportType.MENU
          ? this.generateMenuMarkdown(cocktails, ingredients, groupBy)
          : this.generateCheatSheetMarkdown(cocktails, ingredients);
        filename = `${baseFilename}.md`;
        mimeType = 'text/markdown';
        break;
      case ExportFormat.PDF:
        // For PDF, we'll generate HTML and use the browser's print-to-PDF functionality
        content = type === ExportType.MENU
          ? this.generateMenuHTML(cocktails, ingredients, groupBy)
          : this.generateCheatSheetHTML(cocktails, ingredients);
        this.printToPDF(content, baseFilename);
        return;
    }

    this.downloadFile(content, filename, mimeType);
  }

  /**
   * Generate Menu HTML
   */
  private generateMenuHTML(cocktails: Cocktail[], ingredients: Ingredient[], groupBy: 'spirit' | 'tags'): string {
    const groups = this.groupCocktails(cocktails, ingredients, groupBy);
    
    let html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cocktail Menu</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      max-width: 800px;
      margin: 40px auto;
      padding: 20px;
      background: #f5f5f5;
    }
    h1 {
      text-align: center;
      color: #333;
      border-bottom: 3px solid #333;
      padding-bottom: 10px;
    }
    h2 {
      color: #555;
      margin-top: 30px;
      border-bottom: 2px solid #999;
      padding-bottom: 5px;
    }
    .cocktail {
      background: white;
      padding: 15px;
      margin: 10px 0;
      border-radius: 5px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .cocktail-name {
      font-size: 1.2em;
      font-weight: bold;
      color: #222;
      margin-bottom: 8px;
    }
    .ingredients {
      color: #666;
      line-height: 1.6;
    }
    .ingredients li {
      margin: 3px 0;
    }
    @media print {
      body {
        background: white;
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
      for (const cocktail of groupCocktails) {
        html += `  <div class="cocktail">
    <div class="cocktail-name">${this.escapeHtml(cocktail.name)}</div>
    <ul class="ingredients">
`;
        for (const ing of cocktail.ingredients) {
          const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
          html += `      <li>${this.escapeHtml(ingredientName)} - ${this.escapeHtml(ing.measure)}</li>\n`;
        }
        html += `    </ul>
  </div>
`;
      }
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
      max-width: 900px;
      margin: 40px auto;
      padding: 20px;
      background: #f5f5f5;
    }
    h1 {
      text-align: center;
      color: #333;
      border-bottom: 3px solid #333;
      padding-bottom: 10px;
    }
    .cocktail {
      background: white;
      padding: 20px;
      margin: 20px 0;
      border-radius: 5px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      page-break-inside: avoid;
    }
    .cocktail-name {
      font-size: 1.4em;
      font-weight: bold;
      color: #222;
      margin-bottom: 12px;
    }
    .section-title {
      font-weight: bold;
      color: #555;
      margin-top: 12px;
      margin-bottom: 6px;
    }
    .ingredients, .steps {
      color: #666;
      line-height: 1.8;
    }
    .ingredients li, .steps li {
      margin: 5px 0;
    }
    .notes {
      color: #777;
      font-style: italic;
      margin-top: 10px;
      padding: 10px;
      background: #f9f9f9;
      border-left: 3px solid #ddd;
    }
    @media print {
      body {
        background: white;
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
`;

    for (const cocktail of cocktails) {
      html += `  <div class="cocktail">
    <div class="cocktail-name">${this.escapeHtml(cocktail.name)}</div>
    
    <div class="section-title">Ingredients:</div>
    <ul class="ingredients">
`;
      for (const ing of cocktail.ingredients) {
        const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
        html += `      <li>${this.escapeHtml(ingredientName)} - ${this.escapeHtml(ing.measure)}</li>\n`;
      }
      html += `    </ul>
`;

      if (cocktail.steps && cocktail.steps.length > 0) {
        html += `    
    <div class="section-title">Instructions:</div>
    <ol class="steps">
`;
        for (const step of cocktail.steps) {
          html += `      <li>${this.escapeHtml(step)}</li>\n`;
        }
        html += `    </ol>
`;
      }

      if (cocktail.notes) {
        html += `    
    <div class="notes">
      <strong>Notes:</strong> ${this.escapeHtml(cocktail.notes)}
    </div>
`;
      }

      html += `  </div>
`;
    }

    html += `</body>
</html>`;
    return html;
  }

  /**
   * Generate Menu Markdown
   */
  private generateMenuMarkdown(cocktails: Cocktail[], ingredients: Ingredient[], groupBy: 'spirit' | 'tags'): string {
    const groups = this.groupCocktails(cocktails, ingredients, groupBy);
    
    let markdown = `# Cocktail Menu\n\n`;

    for (const [groupName, groupCocktails] of Object.entries(groups)) {
      markdown += `## ${groupName}\n\n`;
      for (const cocktail of groupCocktails) {
        markdown += `### ${cocktail.name}\n\n`;
        for (const ing of cocktail.ingredients) {
          const ingredientName = this.getIngredientName(ing.ingredientId, ingredients);
          markdown += `- ${ingredientName} - ${ing.measure}\n`;
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
        markdown += `- ${ingredientName} - ${ing.measure}\n`;
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
    groupBy: 'spirit' | 'tags'
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
      // Group by tags
      for (const cocktail of cocktails) {
        if (cocktail.tags && cocktail.tags.length > 0) {
          for (const tag of cocktail.tags) {
            if (!groups[tag]) {
              groups[tag] = [];
            }
            groups[tag].push(cocktail);
          }
        } else {
          if (!groups['Untagged']) {
            groups['Untagged'] = [];
          }
          groups['Untagged'].push(cocktail);
        }
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
