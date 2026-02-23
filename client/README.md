# Jumble Game Client

A minimal React web application for playing the word guessing game.

## Features

- Start a new game with scrambled letters
- Submit word guesses
- Track guessed words and remaining words
- Visual feedback for correct/incorrect guesses
- Responsive design

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Game server running on `http://localhost:8080`

## Installation

```bash
# Navigate to the client directory
cd client

# Install dependencies
npm install
```

## Running the Application

```bash
# Start the development server
npm start
```

The application will open in your browser at `http://localhost:3000`.

**Important**: Make sure the game server is running on `http://localhost:8080` before starting the client.

## How to Play

1. Click "Start Game" to create a new game board
2. You'll see scrambled letters from a word
3. Type words you can make from those letters (minimum 3 characters)
4. Submit your guess
5. Try to find all possible words!

## Build for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

## API Integration

The client communicates with the backend API:

- `GET /api/game/new` - Creates a new game
- `POST /api/game/guess` - Submits a word guess

The proxy configuration in `package.json` routes API requests to `http://localhost:8080`.
