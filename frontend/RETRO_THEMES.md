# Retro 80s Theme Options for CocktailDB

This document presents three retro 80s design drafts for the CocktailDB application. Each theme embodies different aspects of 1980s computer and cyberpunk aesthetics.

## Theme 1: Terminal Green - Classic 80s Computer Terminal
**Style:** Classic monochrome green terminal (like early IBM and DEC terminals)
**Colors:** Black background (#000000) with bright green text (#00ff00)
**Font:** Courier New monospace
**Special Effects:**
- CRT scanline overlay effect
- Green text glow/shadow
- Terminal-style borders and buttons
- High contrast green on black aesthetic

**Files:**
- `styles-theme1-terminal-green.css`

**Screenshots:**
- Cocktails page: ![Theme 1 Cocktails](https://github.com/user-attachments/assets/9b6843b4-4368-431a-8762-43072bf98276)
- Ingredients page: ![Theme 1 Ingredients](https://github.com/user-attachments/assets/230e8dfb-bb0c-4e72-9284-37b693b352f5)

**Inspiration:** Classic UNIX/DOS terminals, The Matrix's opening sequences

---

## Theme 2: Cyberpunk Magenta/Cyan - Neon Cyberpunk Aesthetic
**Style:** Neon-lit cyberpunk (RoboCop, Blade Runner, The Matrix)
**Colors:** Dark blue background (#0a0e27) with cyan (#00ffff) and magenta (#ff00ff)
**Font:** Courier New monospace
**Special Effects:**
- CRT scanline overlay effect
- Dual-color neon glow (cyan and magenta)
- RGB color separation effect
- Gradient borders and highlights
- Intense neon glow on hover states

**Files:**
- `styles-theme2-cyberpunk.css`

**Screenshots:**
- Cocktails page: ![Theme 2 Cocktails](https://github.com/user-attachments/assets/4c30696e-bdaf-442c-8792-8b0e3b55d6f9)
- Ingredients page: ![Theme 2 Ingredients](https://github.com/user-attachments/assets/8cdedeac-ba8f-4901-a37b-68fdb6ff49b4)

**Inspiration:** Cyberpunk movies (RoboCop, Blade Runner), retro arcade games, synthwave aesthetic

---

## Theme 3: Amber Monitor - Vintage Amber CRT Monitor
**Style:** Classic amber monochrome CRT display
**Colors:** Dark brown/black background (#1a0f00) with amber/orange text (#ffb000)
**Font:** Courier New monospace
**Special Effects:**
- CRT scanline overlay effect
- Warm amber text glow
- Vintage phosphor monitor appearance
- Softer, warmer retro aesthetic

**Files:**
- `styles-theme3-amber.css`

**Screenshots:**
- Cocktails page: ![Theme 3 Cocktails](https://github.com/user-attachments/assets/14c4a84d-f0c8-4ca2-86c6-053e89539c30)
- Ingredients page: (Screenshot available upon request)

**Inspiration:** Vintage Hercules Graphics Card monitors, early Apple II displays, warm monochrome terminals

---

## How to Apply a Theme

To apply any of these themes to the application:

1. Copy the desired theme file to replace `styles.css`:
   ```bash
   cp styles-theme1-terminal-green.css styles.css
   # OR
   cp styles-theme2-cyberpunk.css styles.css
   # OR
   cp styles-theme3-amber.css styles.css
   ```

2. The Angular development server will automatically reload with the new theme.

## Common Features Across All Themes

All three themes share these retro characteristics:
- **Monospace font:** Courier New for authentic terminal feel
- **CRT scanline effect:** Simulated cathode ray tube display lines
- **Text glow/shadow:** Phosphor screen glow effect
- **Blocky borders:** No rounded corners, pure 80s angular design
- **High contrast:** Maximum readability like old terminals
- **Uppercase labels:** Terminal-style formatting
- **Retro buttons:** Box-style buttons with borders instead of modern shadows
- **16-color palette:** Limited color schemes reminiscent of 80s computer graphics

## Technical Implementation

Each theme is a complete, self-contained CSS file that:
- Overrides all component styles
- Includes base body/html styling
- Adds ::before pseudo-element for CRT scanline effect
- Uses CSS animations for glow effects
- Maintains full functionality of the application
- Is fully responsive

The themes require no JavaScript changes and work with the existing Angular components.
