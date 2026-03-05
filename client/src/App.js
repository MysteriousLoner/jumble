import React, { useState } from 'react';
import './App.css';
import GameStats from './components/GameStats';
import LetterButtons from './components/LetterButtons';
import InputSection from './components/InputSection';
import GuessedWords from './components/GuessedWords';

function App() {
  const [gameState, setGameState] = useState(null);
  const [guessInput, setGuessInput] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [usedLetters, setUsedLetters] = useState([]);
  const [isGuessedWordsExpanded, setIsGuessedWordsExpanded] = useState(false);

  const startNewGame = async () => {
    setLoading(true);
    setMessage('');
    try {
      const response = await fetch('/api/game/new');
      const data = await response.json();
      setGameState(data);
      setGuessInput('');
      setUsedLetters([]);
      setMessage(data.result);
    } catch (error) {
      setMessage('Error starting new game: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleLetterClick = (letter, index) => {
    if (usedLetters.includes(index) || loading || gameState.remaining_words === 0) return;
    setGuessInput(prev => prev + letter);
    setUsedLetters(prev => [...prev, index]);
  };

  const handleReset = () => {
    setGuessInput('');
    setUsedLetters([]);
  };

  const submitGuess = async () => {
    if (!guessInput.trim() || !gameState) return;

    setLoading(true);
    setMessage('');
    try {
      const response = await fetch('/api/game/guess', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          id: gameState.id,
          word: guessInput.trim().toLowerCase(),
        }),
      });
      const data = await response.json();
      setGameState(data);
      setMessage(data.result);
      setGuessInput('');
      setUsedLetters([]);
    } catch (error) {
      setMessage('Error submitting guess: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      {!gameState && (
        <div className="welcome-screen">
          <h1 className="welcome-title">JUMBLE</h1>
          <button 
            onClick={startNewGame} 
            disabled={loading}
            className="btn btn-start"
          >
            START GAME
          </button>
        </div>
      )}

      {gameState && (
        <>
          <header className="App-header">
            <h1>JUMBLE</h1>
            <button 
              onClick={startNewGame} 
              disabled={loading}
              className="btn btn-new-game"
            >
              NEW GAME
            </button>
          </header>

          <main className="App-main">
            <div className="game-container">
              {/* Top: Game Stats */}
              <GameStats 
                guessedWordsCount={gameState.guessed_words?.length || 0}
                remainingWords={gameState.remaining_words}
                totalWords={gameState.total_words}
              />

              {message && (
                <div className="message">
                  {message.toUpperCase()}
                </div>
              )}

              {/* Middle: Scrambled Letter Buttons */}
              <LetterButtons 
                scrambleWord={gameState.scramble_word}
                usedLetters={usedLetters}
                loading={loading}
                remainingWords={gameState.remaining_words}
                onLetterClick={handleLetterClick}
              />

              {/* Bottom: Input Field Display and Controls */}
              <InputSection 
                guessInput={guessInput}
                loading={loading}
                remainingWords={gameState.remaining_words}
                onReset={handleReset}
                onSubmit={submitGuess}
              />

              {/* Guessed Words */}
              <GuessedWords 
                guessedWords={gameState.guessed_words}
                isExpanded={isGuessedWordsExpanded}
                onToggle={() => setIsGuessedWordsExpanded(!isGuessedWordsExpanded)}
              />

              {gameState.remaining_words === 0 && (
                <div className="game-over">
                  COMPLETE!
                </div>
              )}
            </div>
          </main>
        </>
      )}
    </div>
  );
}

export default App;
