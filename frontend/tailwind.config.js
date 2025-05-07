/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          light: '#4F46E5',
          DEFAULT: '#4338CA',
          dark: '#3730A3',
        },
        secondary: {
          light: '#0EA5E9',
          DEFAULT: '#0284C7',
          dark: '#0369A1',
        },
        success: {
          light: '#22C55E',
          DEFAULT: '#16A34A',
          dark: '#15803D',
        },
        danger: {
          light: '#EF4444',
          DEFAULT: '#DC2626',
          dark: '#B91C1C',
        },
        warning: {
          light: '#F59E0B',
          DEFAULT: '#D97706',
          dark: '#B45309',
        },
      },
    },
  },
  plugins: [],
}