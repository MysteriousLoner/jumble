import React, { useState } from 'react';
import './App.css';

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
              <div className="game-stats">
                <div className="stat">
                  <div className="stat-value">{gameState.guessed_words?.length || 0}</div>
                  <div className="stat-label">FOUND</div>
                </div>
                <div className="stat">
                  <div className="stat-value">{gameState.remaining_words}</div>
                  <div className="stat-label">REMAINING</div>
                </div>
                <div className="stat">
                  <div className="stat-value">{gameState.total_words}</div>
                  <div className="stat-label">TOTAL</div>
                </div>
              </div>

              {message && (
                <div className="message">
                  {message.toUpperCase()}
                </div>
              )}

              {/* Middle: Scrambled Letter Buttons */}
              <div className="letter-buttons">
                {gameState.scramble_word.split('').map((letter, index) => (
                  <button
                    key={index}
                    onClick={() => handleLetterClick(letter, index)}
                    disabled={usedLetters.includes(index) || loading || gameState.remaining_words === 0}
                    className={`letter-btn ${usedLetters.includes(index) ? 'used' : ''}`}
                  >
                    {letter.toUpperCase()}
                  </button>
                ))}
              </div>

              {/* Bottom: Input Field Display and Controls */}
              <div className="input-section">
                <div className="input-display">
                  {guessInput.toUpperCase() || <span className="placeholder">CLICK LETTERS ABOVE</span>}
                </div>
                <div className="control-buttons">
                  <button 
                    onClick={handleReset}
                    disabled={!guessInput || loading}
                    className="btn btn-reset"
                  >
                    RESET
                  </button>
                  <button 
                    onClick={submitGuess}
                    disabled={loading || !guessInput.trim() || gameState.remaining_words === 0}
                    className="btn btn-enter"
                  >
                    ENTER
                  </button>
                </div>
              </div>

              {/* Guessed Words */}
              {gameState.guessed_words && gameState.guessed_words.length > 0 && (
                <div className="guessed-words">
                  <button 
                    className="guessed-toggle"
                    onClick={() => setIsGuessedWordsExpanded(!isGuessedWordsExpanded)}
                  >
                    <span>GUESSED WORDS ({gameState.guessed_words.length})</span>
                    <span className={`toggle-icon ${isGuessedWordsExpanded ? 'expanded' : ''}`}>▼</span>
                  </button>
                  {isGuessedWordsExpanded && (
                    <div className="word-list">
                      {gameState.guessed_words.map((word, index) => (
                        <span key={index} className="guessed-word">{word.toUpperCase()}</span>
                      ))}
                    </div>
                  )}
                </div>
              )}

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
