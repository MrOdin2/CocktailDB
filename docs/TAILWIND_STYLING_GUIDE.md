# Tailwind CSS Styling Guide for CocktailDB

## Overview

CocktailDB has been migrated to use [Tailwind CSS](https://tailwindcss.com/) v3.4 with [daisyUI](https://daisyui.com/) for consistent theming and styling. This document explains the styling approach and how to work with the new system.

## Table of Contents

1. [Architecture](#architecture)
2. [Theme System](#theme-system)
3. [Styling Guidelines](#styling-guidelines)
4. [Component Patterns](#component-patterns)
5. [Responsive Design](#responsive-design)
6. [Adding New Themes](#adding-new-themes)
7. [Migration Status](#migration-status)

## Architecture

### Technology Stack

- **Tailwind CSS v3.4.17**: Utility-first CSS framework
- **daisyUI**: Component library and theme system for Tailwind
- **PostCSS**: CSS processor (handles Tailwind compilation)
- **Autoprefixer**: Adds vendor prefixes automatically

### File Structure

```
frontend/
├── src/
│   ├── styles.css                          # Main styles with Tailwind directives
│   ├── app/
│   │   ├── components/
│   │   │   ├── admin/
│   │   │   │   └── admin-shared.css        # Shared admin component styles
│   │   │   ├── visitor/
│   │   │   │   └── visitor-shared.css      # Shared visitor component styles
│   │   │   └── [component]/
│   │   │       └── component.css           # Component-specific styles
│   │   └── services/
│   │       └── theme.service.ts            # Theme switching logic
├── tailwind.config.js                      # Tailwind configuration
└── postcss.config.js                       # PostCSS configuration
```

### Style Loading Order

1. **styles.css**: Main entry point
   - Imports shared CSS files
   - Defines Tailwind layers (@tailwind base, components, utilities)
   - Defines base styles and retro theme effects
2. **admin-shared.css**: Common admin component styles
3. **visitor-shared.css**: Common visitor component styles
4. **Component CSS**: Component-specific styles

## Theme System

### Available Themes

CocktailDB supports 4 themes, all configured in `tailwind.config.js`:

#### 1. Basic Theme (Default)
- **Style**: Professional, minimal
- **Colors**: White background, dark gray text
- **Font**: Arial, Helvetica, sans-serif
- **Special Effects**: None
- **Use Case**: Professional environments, maximum readability

#### 2. Terminal Green Theme
- **Style**: Classic 80s monochrome terminal
- **Colors**: Black background (#000000), bright green text (#00ff00)
- **Font**: Courier New monospace
- **Special Effects**: CRT scanline overlay, text glow/shadow
- **Use Case**: Retro/nostalgic aesthetic, Unix/DOS terminal feel

#### 3. Cyberpunk Theme
- **Style**: Neon cyberpunk aesthetic
- **Colors**: Dark blue background (#0a0e27), cyan (#00ffff) and magenta (#ff00ff)
- **Font**: Courier New monospace
- **Special Effects**: CRT scanline overlay, intense neon glow
- **Use Case**: Futuristic/cyberpunk aesthetic, high contrast

#### 4. Amber Monitor Theme
- **Style**: Vintage amber CRT display
- **Colors**: Dark brown background (#1a0f00), amber/orange text (#ffb000)
- **Font**: Courier New monospace
- **Special Effects**: CRT scanline overlay, warm amber glow
- **Use Case**: Warm retro aesthetic, vintage computing feel

### Theme Switching

Themes are switched using the `ThemeService`:

```typescript
// In a component
constructor(private themeService: ThemeService) {}

setTheme(theme: 'basic' | 'terminal-green' | 'cyberpunk' | 'amber') {
  this.themeService.setTheme(theme);
}
```

The service applies themes by setting the `data-theme` attribute on the `<html>` element, which daisyUI uses to apply theme-specific color values.

### How Themes Work

daisyUI themes use CSS custom properties (CSS variables) for colors:

- `--p`: Primary color (buttons, links, highlights)
- `--s`: Secondary color
- `--a`: Accent color
- `--n`: Neutral color
- `--b1`: Base background color
- `--b2`: Base background color (slightly darker)
- `--b3`: Base background color (even darker)
- `--bc`: Base content (text) color
- `--in`: Info color
- `--su`: Success color
- `--wa`: Warning color
- `--er`: Error color

These are accessed in CSS using `oklch(var(--variableName))`:

```css
.my-element {
  background-color: oklch(var(--b1));
  color: oklch(var(--bc));
  border-color: oklch(var(--p));
}
```

## Styling Guidelines

### 1. Prefer Tailwind Utility Classes

Use Tailwind utility classes directly in HTML templates:

```html
<!-- Good -->
<div class="flex items-center gap-4 p-4 bg-base-100 rounded-lg">
  <h2 class="text-2xl font-bold">Title</h2>
</div>

<!-- Avoid (unless reusable across many components) -->
<div class="my-custom-container">
  <h2 class="my-custom-heading">Title</h2>
</div>
```

### 2. Use @apply for Reusable Components

Create reusable component classes in shared CSS files using `@apply`:

```css
/* In admin-shared.css or visitor-shared.css */
.container {
  @apply max-w-7xl mx-auto px-4;
}

.header-section {
  @apply flex justify-between items-center mb-5 gap-4 flex-wrap;
}
```

### 3. Access Theme Colors Correctly

Always use `oklch(var(--colorName))` to access daisyUI theme colors:

```css
/* Correct */
.my-button {
  background-color: oklch(var(--p));
  color: oklch(var(--pc));
  border-color: oklch(var(--p));
}

/* Incorrect - won't adapt to themes */
.my-button {
  background-color: #00ff00;
  color: #000000;
}
```

### 4. Responsive Design

Use Tailwind's responsive prefixes:

```html
<!-- Mobile first, then tablet, then desktop -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  <div class="text-sm md:text-base lg:text-lg">Responsive text</div>
</div>
```

Breakpoints:
- `sm`: 640px (rarely used, as mobile-first)
- `md`: 768px (tablet)
- `lg`: 1024px (desktop)
- `xl`: 1280px (large desktop)
- `2xl`: 1536px (extra large)

### 5. Component-Specific Styles

For styles unique to a single component, keep them in the component's CSS file:

```css
/* cocktails.component.css */
.ingredient-inputs {
  @apply flex gap-3 mb-3 flex-wrap;
}

.ingredient-inputs select {
  @apply flex-1 min-w-[150px];
}
```

## Component Patterns

### Buttons

```html
<!-- Primary button -->
<button class="btn btn-primary">Primary Action</button>

<!-- Secondary button -->
<button class="btn btn-secondary">Secondary Action</button>

<!-- Custom styled button -->
<button class="px-4 py-2 border-2 rounded cursor-pointer transition-all hover:scale-105" 
        [style.border-color]="'oklch(var(--p))'">
  Custom Button
</button>
```

### Forms

```html
<div class="form-group">
  <label class="block mb-2 font-bold">Label</label>
  <input type="text" 
         class="w-full p-3 rounded border"
         [style.border-color]="'oklch(var(--b3))'"
         [style.background-color]="'oklch(var(--b1))'">
</div>
```

### Cards

```html
<div class="border-2 rounded-lg p-6 transition-all hover:shadow-lg"
     [style.border-color]="'oklch(var(--p))'">
  <h3 class="text-xl font-bold mb-4">Card Title</h3>
  <p>Card content</p>
</div>
```

### Modals

```html
<div class="modal-backdrop">
  <div class="modal-content max-w-2xl w-full rounded-lg"
       [style.background-color]="'oklch(var(--b1))'">
    <div class="modal-header">
      <h3 class="text-xl font-bold">Modal Title</h3>
      <button class="close-button">&times;</button>
    </div>
    <div class="modal-body">
      <!-- Content -->
    </div>
    <div class="modal-actions">
      <button class="btn btn-secondary">Cancel</button>
      <button class="btn btn-primary">Confirm</button>
    </div>
  </div>
</div>
```

## Responsive Design

### Mobile-First Approach

Always start with mobile styles, then add tablet and desktop styles:

```html
<!-- Stacks on mobile, 2 columns on tablet, 3 on desktop -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  ...
</div>

<!-- Full width on mobile, fixed width on tablet+ -->
<div class="w-full md:w-auto">
  ...
</div>

<!-- Hide on mobile, show on tablet+ -->
<div class="hidden md:block">
  Tablet and desktop only
</div>
```

### Touch-Friendly Sizing

Interactive elements should be at least 44x44 pixels on mobile:

```css
/* Automatically applied on mobile */
@media (max-width: 1023px) {
  button, a, input[type="checkbox"], input[type="radio"], select {
    @apply min-h-[44px] min-w-[44px];
  }
}
```

## Adding New Themes

To add a new theme:

1. **Define the theme in `tailwind.config.js`**:

```javascript
daisyui: {
  themes: [
    {
      // ... existing themes ...
      "my-new-theme": {
        "primary": "#ff6600",
        "secondary": "#00ff66",
        "accent": "#6600ff",
        "neutral": "#1a1a1a",
        "base-100": "#000000",
        "base-200": "#111111",
        "base-300": "#222222",
        "info": "#00ccff",
        "success": "#00ff00",
        "warning": "#ffaa00",
        "error": "#ff0000",
        "--rounded-box": "0.5rem",
        "--rounded-btn": "0.25rem",
        "--rounded-badge": "1rem",
        "--animation-btn": "0.2s",
        "--animation-input": "0.2s",
        "--btn-text-case": "none",
        "--border-btn": "1px",
      },
    },
  ],
}
```

2. **Add special effects (if needed) in `styles.css`**:

```css
@layer components {
  [data-theme="my-new-theme"] body::before {
    /* Add background effects */
  }
  
  [data-theme="my-new-theme"] {
    h1, h2, h3, h4, h5, h6 {
      /* Add text effects */
      text-shadow: 0 0 10px currentColor;
    }
  }
}
```

3. **Update `ThemeService` type**:

```typescript
export type Theme = 'basic' | 'terminal-green' | 'cyberpunk' | 'amber' | 'my-new-theme';
```

4. **Add theme to settings component**:

```typescript
themes = [
  // ... existing themes ...
  {
    id: 'my-new-theme' as Theme,
    name: 'My New Theme',
    description: 'Description of the new theme',
    preview: 'Color preview'
  }
];
```

## Migration Status

### Completed
- ✅ Core Tailwind setup and configuration
- ✅ daisyUI theme system integration
- ✅ Theme service updated for data-theme switching
- ✅ Global styles migrated to Tailwind directives
- ✅ admin-shared.css converted to Tailwind utilities
- ✅ visitor-shared.css converted to Tailwind utilities
- ✅ CRT effects and retro theme styling preserved

### In Progress
- 🔄 Component-specific CSS migration (admin components)
- 🔄 Component-specific CSS migration (visitor components)
- 🔄 Component-specific CSS migration (barkeeper components)
- 🔄 Component-specific CSS migration (shared components)

### Pending
- ⏳ Remove obsolete theme CSS files
- ⏳ Update component templates to use Tailwind classes
- ⏳ Testing across all themes and viewports
- ⏳ Performance optimization (bundle size)

## Best Practices

1. **Use daisyUI components when possible**: They're pre-styled and theme-aware
2. **Keep @apply usage minimal**: Prefer utility classes in templates
3. **Use semantic color names**: Use `primary`, `secondary`, etc. instead of specific colors
4. **Test in all themes**: Ensure your components work in all 4 themes
5. **Mobile-first responsive design**: Always start with mobile layout
6. **Maintain accessibility**: Use proper ARIA labels and semantic HTML
7. **Keep specificity low**: Avoid deep nesting and !important
8. **Document custom classes**: Add comments for non-obvious styling choices

## Troubleshooting

### Theme not applying
- Check that `data-theme` attribute is set on `<html>` element
- Verify theme name matches exactly (case-sensitive)
- Ensure daisyUI is properly configured in tailwind.config.js

### Colors not changing with theme
- Use `oklch(var(--colorName))` instead of hardcoded colors
- Check that you're using valid daisyUI color variables

### Tailwind classes not working
- Verify Tailwind is processing your files (check `content` in tailwind.config.js)
- Make sure classes are not being purged (avoid dynamic class names)
- Run `npm run build` to regenerate styles

### Build size too large
- Review unused Tailwind utilities
- Consider purging unused daisyUI themes
- Optimize component CSS files

## Resources

- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [daisyUI Documentation](https://daisyui.com/)
- [daisyUI Themes](https://daisyui.com/docs/themes/)
- [Tailwind Play (online playground)](https://play.tailwindcss.com/)

## Support

For questions or issues related to styling:
1. Check this guide first
2. Review Tailwind CSS documentation
3. Check daisyUI theme documentation
4. Create an issue in the repository with details
