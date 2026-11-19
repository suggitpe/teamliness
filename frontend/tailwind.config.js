/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        glass: {
          surface: 'rgba(15, 23, 42, 0.55)',
          elevated: 'rgba(30, 41, 59, 0.65)',
          border: 'rgba(255, 255, 255, 0.15)',
        },
      },
    },
  },
  plugins: [],
};
