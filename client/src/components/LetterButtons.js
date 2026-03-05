import React from 'react';

function LetterButtons({ scrambleWord, usedLetters, loading, remainingWords, onLetterClick }) {
  return (
    <div className="letter-buttons">
      {scrambleWord.split('').map((letter, index) => (
        <button
          key={index}
          onClick={() => onLetterClick(letter, index)}
          disabled={usedLetters.includes(index) || loading || remainingWords === 0}
          className={`letter-btn ${usedLetters.includes(index) ? 'used' : ''}`}
        >
          {letter.toUpperCase()}
        </button>
      ))}
    </div>
  );
}

export default LetterButtons;
