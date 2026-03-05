import React from 'react';

function GuessedWords({ guessedWords, isExpanded, onToggle }) {
  if (!guessedWords || guessedWords.length === 0) {
    return null;
  }

  return (
    <div className="guessed-words">
      <button 
        className="guessed-toggle"
        onClick={onToggle}
      >
        <span>GUESSED WORDS ({guessedWords.length})</span>
        <span className={`toggle-icon ${isExpanded ? 'expanded' : ''}`}>▼</span>
      </button>
      {isExpanded && (
        <div className="word-list">
          {guessedWords.map((word, index) => (
            <span key={index} className="guessed-word">{word.toUpperCase()}</span>
          ))}
        </div>
      )}
    </div>
  );
}

export default GuessedWords;
