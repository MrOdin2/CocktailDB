/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        // Custom colors for retro themes will be defined via CSS variables
      },
      fontFamily: {
        'mono': ['Courier New', 'Courier', 'monospace'],
        'sans': ['Arial', 'Helvetica', 'sans-serif'],
      },
    },
  },
  plugins: [
    require('daisyui'),
  ],
  daisyui: {
    themes: [
      {
        basic: {
          "primary": "oklch(0.35 0 0)",           // #333333
          "primary-content": "oklch(1 0 0)",      // #ffffff  
          "secondary": "oklch(1 0 0)",            // #ffffff
          "secondary-content": "oklch(0.35 0 0)", // #333333
          "accent": "oklch(0.35 0 0)",            // #333333
          "accent-content": "oklch(1 0 0)",       // #ffffff
          "neutral": "oklch(0.96 0 0)",           // #f5f5f5
          "neutral-content": "oklch(0.35 0 0)",   // #333333
          "base-100": "oklch(1 0 0)",             // #ffffff
          "base-200": "oklch(0.98 0 0)",          // #f9f9f9
          "base-300": "oklch(0.96 0 0)",          // #f5f5f5
          "base-content": "oklch(0.35 0 0)",      // #333333
          "info": "oklch(0.35 0 0)",              // #333333
          "info-content": "oklch(1 0 0)",         // #ffffff
          "success": "oklch(0.60 0.20 145)",      // #00aa00
          "success-content": "oklch(1 0 0)",      // #ffffff
          "warning": "oklch(0.75 0.20 85)",       // #ffaa00
          "warning-content": "oklch(0 0 0)",      // #000000
          "error": "oklch(0.55 0.25 25)",         // #cc0000
          "error-content": "oklch(1 0 0)",        // #ffffff
          "--rounded-box": "0.5rem",
          "--rounded-btn": "0.25rem",
          "--rounded-badge": "1rem",
        },
        "terminal-green": {
          "primary": "oklch(0.90 0.30 145)",      // #00ff00
          "primary-content": "oklch(0 0 0)",      // #000000
          "secondary": "oklch(0.50 0.20 145)",    // #008800
          "secondary-content": "oklch(0.90 0.30 145)", // #00ff00
          "accent": "oklch(0.90 0.30 145)",       // #00ff00
          "accent-content": "oklch(0 0 0)",       // #000000
          "neutral": "oklch(0.10 0.05 145)",      // #001100
          "neutral-content": "oklch(0.90 0.30 145)", // #00ff00
          "base-100": "oklch(0 0 0)",             // #000000
          "base-200": "oklch(0.10 0.05 145)",     // #001100
          "base-300": "oklch(0.15 0.05 145)",     // #002200
          "base-content": "oklch(0.90 0.30 145)", // #00ff00
          "info": "oklch(0.90 0.30 145)",         // #00ff00
          "info-content": "oklch(0 0 0)",         // #000000
          "success": "oklch(0.90 0.30 145)",      // #00ff00
          "success-content": "oklch(0 0 0)",      // #000000
          "warning": "oklch(0.95 0.25 100)",      // #ffff00
          "warning-content": "oklch(0 0 0)",      // #000000
          "error": "oklch(0.55 0.30 25)",         // #ff0000
          "error-content": "oklch(1 0 0)",        // #ffffff
          "--rounded-box": "0",
          "--rounded-btn": "0",
          "--rounded-badge": "0",
        },
        cyberpunk: {
          "primary": "oklch(0.85 0.20 195)",      // #00ffff
          "primary-content": "oklch(0.20 0.05 250)", // #0a0e27
          "secondary": "oklch(0.70 0.30 320)",    // #ff00ff
          "secondary-content": "oklch(0.20 0.05 250)", // #0a0e27
          "accent": "oklch(0.85 0.20 195)",       // #00ffff
          "accent-content": "oklch(0.20 0.05 250)", // #0a0e27
          "neutral": "oklch(0.20 0.05 250)",      // #0a0e27
          "neutral-content": "oklch(0.85 0.20 195)", // #00ffff
          "base-100": "oklch(0.20 0.05 250)",     // #0a0e27
          "base-200": "oklch(0.25 0.05 250)",     // #141933
          "base-300": "oklch(0.30 0.05 250)",     // #1e2440
          "base-content": "oklch(0.85 0.20 195)", // #00ffff
          "info": "oklch(0.85 0.20 195)",         // #00ffff
          "info-content": "oklch(0.20 0.05 250)", // #0a0e27
          "success": "oklch(0.90 0.30 145)",      // #00ff00
          "success-content": "oklch(0.20 0.05 250)", // #0a0e27
          "warning": "oklch(0.95 0.25 100)",      // #ffff00
          "warning-content": "oklch(0.20 0.05 250)", // #0a0e27
          "error": "oklch(0.60 0.30 5)",          // #ff0066
          "error-content": "oklch(1 0 0)",        // #ffffff
          "--rounded-box": "0",
          "--rounded-btn": "0",
          "--rounded-badge": "0",
        },
        amber: {
          "primary": "oklch(0.75 0.15 75)",       // #ffb000
          "primary-content": "oklch(0.15 0.05 75)", // #1a0f00
          "secondary": "oklch(0.60 0.15 75)",     // #cc8800
          "secondary-content": "oklch(0.15 0.05 75)", // #1a0f00
          "accent": "oklch(0.75 0.15 75)",        // #ffb000
          "accent-content": "oklch(0.15 0.05 75)", // #1a0f00
          "neutral": "oklch(0.15 0.05 75)",       // #1a0f00
          "neutral-content": "oklch(0.75 0.15 75)", // #ffb000
          "base-100": "oklch(0.15 0.05 75)",      // #1a0f00
          "base-200": "oklch(0.20 0.05 75)",      // #2a1f00
          "base-300": "oklch(0.25 0.05 75)",      // #3a2f00
          "base-content": "oklch(0.75 0.15 75)",  // #ffb000
          "info": "oklch(0.75 0.15 75)",          // #ffb000
          "info-content": "oklch(0.15 0.05 75)",  // #1a0f00
          "success": "oklch(0.70 0.15 75)",       // #ffaa00
          "success-content": "oklch(0.15 0.05 75)", // #1a0f00
          "warning": "oklch(0.80 0.15 90)",       // #ffcc00
          "warning-content": "oklch(0.15 0.05 75)", // #1a0f00
          "error": "oklch(0.60 0.20 40)",         // #ff6600
          "error-content": "oklch(1 0 0)",        // #ffffff
          "--rounded-box": "0",
          "--rounded-btn": "0",
          "--rounded-badge": "0",
        },
      },
    ],
  },
}
