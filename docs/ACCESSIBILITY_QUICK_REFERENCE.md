# Accessibility Quick Reference Guide

A quick reference for developers working on CocktailDB to ensure accessibility best practices.

For the complete assessment and improvement plan, see [ACCESSIBILITY.md](ACCESSIBILITY.md).

---

## Quick Checks Before Committing

### ✅ Forms
- [ ] Every `<input>` has an associated `<label>` with matching `for` and `id`
- [ ] Required fields are marked with `required` attribute
- [ ] Error messages use `role="alert"` or `aria-live="assertive"`
- [ ] Form groups use `<fieldset>` and `<legend>` where appropriate

### ✅ Buttons and Links
- [ ] Icon-only buttons have `aria-label` or visible text
- [ ] Buttons use `<button>` element, not `<div>` or `<span>`
- [ ] Links use `<a>` element with `href` attribute
- [ ] Active navigation items have `aria-current="page"`

### ✅ Interactive Elements
- [ ] All interactive elements are keyboard accessible
- [ ] Focus order is logical
- [ ] Focus is visible (outline or custom style)
- [ ] No keyboard traps
- [ ] Tooltips and dropdowns are accessible

### ✅ Semantic HTML
- [ ] Use `<header>`, `<nav>`, `<main>`, `<aside>`, `<footer>` instead of `<div>`
- [ ] Heading hierarchy is logical (h1 → h2 → h3, no skipping)
- [ ] Lists use `<ul>`, `<ol>`, or `<dl>` appropriately
- [ ] Tables use `<table>`, `<caption>`, `<thead>`, `<tbody>`, `<th>`, `<td>`

### ✅ ARIA
- [ ] ARIA landmarks added: `role="banner"`, `role="main"`, `role="navigation"`
- [ ] Dynamic content uses `aria-live` regions
- [ ] Modals have `role="dialog"`, `aria-modal="true"`, `aria-labelledby`
- [ ] Expandable sections have `aria-expanded`

### ✅ Visual
- [ ] Text meets 4.5:1 contrast ratio (3:1 for large text)
- [ ] Don't rely on color alone to convey information
- [ ] Focus indicators are clearly visible
- [ ] Content is readable at 200% zoom

---

## Common Patterns

### Pattern 1: Form Input with Label
```html
<div class="form-group">
  <label for="cocktail-name">Cocktail Name *</label>
  <input 
    type="text" 
    id="cocktail-name" 
    name="cocktailName"
    [(ngModel)]="cocktail.name"
    required
    aria-required="true"
    aria-describedby="name-help">
  <small id="name-help">Enter a unique name for this cocktail</small>
</div>
```

### Pattern 2: Icon Button
```html
<!-- Bad -->
<button (click)="delete()">🗑️</button>

<!-- Good -->
<button (click)="delete()" aria-label="Delete cocktail">
  <span aria-hidden="true">🗑️</span>
  <span class="sr-only">Delete</span>
</button>
```

### Pattern 3: Navigation Menu
```html
<nav role="navigation" aria-label="Main navigation">
  <ul>
    <li><a routerLink="/admin/ingredients" routerLinkActive="active" [attr.aria-current]="isActive('/admin/ingredients') ? 'page' : null">Ingredients</a></li>
    <li><a routerLink="/admin/cocktails" routerLinkActive="active" [attr.aria-current]="isActive('/admin/cocktails') ? 'page' : null">Cocktails</a></li>
  </ul>
</nav>
```

### Pattern 4: Modal Dialog
```html
<div 
  class="modal-backdrop" 
  *ngIf="isOpen"
  (click)="onBackdropClick()">
  <div 
    class="modal-content" 
    role="dialog" 
    aria-modal="true" 
    aria-labelledby="modal-title"
    (click)="$event.stopPropagation()">
    <div class="modal-header">
      <h2 id="modal-title">{{ title }}</h2>
      <button 
        type="button" 
        class="close-button" 
        aria-label="Close dialog"
        (click)="close()">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <ng-content></ng-content>
    </div>
  </div>
</div>
```

### Pattern 5: Data Table
```html
<table>
  <caption>List of Cocktail Ingredients</caption>
  <thead>
    <tr>
      <th scope="col">Name</th>
      <th scope="col">Type</th>
      <th scope="col">ABV (%)</th>
      <th scope="col">In Stock</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let ingredient of ingredients">
      <td>{{ ingredient.name }}</td>
      <td>{{ ingredient.type }}</td>
      <td>{{ ingredient.abv }}</td>
      <td>
        <input 
          type="checkbox" 
          [checked]="ingredient.inStock"
          [attr.aria-label]="'Mark ' + ingredient.name + ' as ' + (ingredient.inStock ? 'out of stock' : 'in stock')"
          (change)="toggleStock(ingredient)">
      </td>
      <td>
        <button class="btn-edit" (click)="edit(ingredient)" [attr.aria-label]="'Edit ' + ingredient.name">Edit</button>
        <button class="btn-delete" (click)="delete(ingredient)" [attr.aria-label]="'Delete ' + ingredient.name">Delete</button>
      </td>
    </tr>
  </tbody>
</table>
```

### Pattern 6: Live Region for Updates
```html
<!-- Hidden live region for announcements -->
<div 
  aria-live="polite" 
  aria-atomic="true" 
  class="sr-only">
  {{ statusMessage }}
</div>

<!-- Example usage in component -->
statusMessage = 'Ingredient "Vodka" marked as in stock';
```

### Pattern 7: Loading State
```html
<div *ngIf="isLoading" role="status" aria-live="polite">
  <div class="spinner" aria-hidden="true"></div>
  <span class="sr-only">Loading cocktails, please wait...</span>
</div>
```

### Pattern 8: Error Message
```html
<div 
  *ngIf="errorMessage" 
  role="alert" 
  aria-live="assertive"
  class="error-message">
  {{ errorMessage }}
</div>
```

---

## Essential CSS Classes

### Screen Reader Only
```css
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.sr-only-focusable:focus {
  position: static;
  width: auto;
  height: auto;
  padding: inherit;
  margin: inherit;
  overflow: visible;
  clip: auto;
  white-space: normal;
}
```

### Skip Link
```css
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  background: #000;
  color: #fff;
  padding: 8px;
  text-decoration: none;
  z-index: 100;
}

.skip-link:focus {
  top: 0;
}
```

### Focus Styles
```css
*:focus {
  outline: 3px solid var(--focus-color, #00ff00);
  outline-offset: 2px;
}

*:focus:not(:focus-visible) {
  outline: none;
}

*:focus-visible {
  outline: 3px solid var(--focus-color, #00ff00);
  outline-offset: 2px;
}

/* High contrast mode support */
@media (prefers-contrast: high) {
  *:focus-visible {
    outline-width: 5px;
  }
}
```

---

## Testing Checklist

### Keyboard Testing
```
Tab         → Move focus forward
Shift+Tab   → Move focus backward
Enter       → Activate buttons/links
Space       → Toggle checkboxes/buttons
Esc         → Close dialogs/menus
Arrow Keys  → Navigate within menus/lists
```

### Screen Reader Testing Commands (NVDA)
```
NVDA+Down   → Read next item
NVDA+Up     → Read previous item
H           → Next heading
K           → Next link
B           → Next button
F           → Next form field
T           → Next table
Insert+F7   → List all elements
```

### Browser DevTools
1. **Chrome DevTools**
   - Open DevTools → Lighthouse → Run Accessibility audit
   - Inspect element → Accessibility pane
   - Enable "Accessibility" in rendering tab

2. **Firefox DevTools**
   - Open DevTools → Accessibility Inspector
   - Check "Show Tabbing Order"

---

## Color Contrast Requirements

### WCAG 2.1 Level AA
- **Normal text** (< 18pt or < 14pt bold): 4.5:1 minimum
- **Large text** (≥ 18pt or ≥ 14pt bold): 3:1 minimum
- **UI Components**: 3:1 minimum
- **Graphical elements**: 3:1 minimum

### Testing Tools
- [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)
- Chrome DevTools Color Picker
- [Colour Contrast Analyser](https://www.tpgi.com/color-contrast-checker/)

### Current Theme Verification Needed
| Element | Foreground | Background | Ratio | Status |
|---------|-----------|------------|-------|---------|
| Body text | #00ff00 | #000000 | 12.6:1 | ✅ Pass |
| Links | Need check | Need check | ? | ⚠️ Verify |
| Buttons | Need check | Need check | ? | ⚠️ Verify |
| Disabled state | Need check | Need check | ? | ⚠️ Verify |

---

## Common Mistakes to Avoid

### ❌ Don't
```html
<!-- Using div as button -->
<div (click)="submit()">Submit</div>

<!-- Missing label -->
<input type="text" placeholder="Enter name">

<!-- Icon button without label -->
<button><i class="icon-delete"></i></button>

<!-- Click handler on non-interactive element -->
<span (click)="doSomething()">Click me</span>

<!-- Color only to indicate state -->
<span style="color: red;">Error</span>
```

### ✅ Do
```html
<!-- Use semantic button -->
<button type="button" (click)="submit()">Submit</button>

<!-- Include label -->
<label for="name-input">Name</label>
<input type="text" id="name-input" placeholder="Enter name">

<!-- Icon button with label -->
<button aria-label="Delete item">
  <i class="icon-delete" aria-hidden="true"></i>
</button>

<!-- Make it a button -->
<button type="button" (click)="doSomething()">Click me</button>

<!-- Use icon + text for state -->
<span role="status">
  <i class="icon-error" aria-hidden="true"></i>
  Error: Invalid input
</span>
```

---

## Resources

### Testing Tools
- **axe DevTools**: Browser extension for automated testing
- **WAVE**: Web accessibility evaluation tool
- **Lighthouse**: Built into Chrome DevTools
- **NVDA**: Free Windows screen reader
- **VoiceOver**: Built into macOS/iOS

### Documentation
- [A11Y Project](https://www.a11yproject.com/)
- [WebAIM](https://webaim.org/)
- [WCAG Quick Reference](https://www.w3.org/WAI/WCAG21/quickref/)
- [ARIA Authoring Practices](https://www.w3.org/WAI/ARIA/apg/)
- [MDN Accessibility](https://developer.mozilla.org/en-US/docs/Web/Accessibility)

### Angular-Specific
- [Angular Accessibility](https://angular.dev/best-practices/a11y)
- [Angular CDK A11y](https://material.angular.io/cdk/a11y/overview)

---

## Getting Help

If you're unsure about accessibility:
1. Check this guide first
2. Review [ACCESSIBILITY.md](ACCESSIBILITY.md) for detailed guidance
3. Use automated testing tools (axe, WAVE, Lighthouse)
4. Test with keyboard and screen reader
5. Ask a team member for review

Remember: **Accessibility is not optional. It's a requirement.**

