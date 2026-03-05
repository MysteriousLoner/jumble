import React from 'react';

function GameStats({ guessedWordsCount, remainingWords, totalWords }) {
  return (
    <div className="game-stats">
      <div className="stat">
        <div className="stat-value">{guessedWordsCount}</div>
        <div className="stat-label">FOUND</div>
      </div>
      <div className="stat">
        <div className="stat-value">{remainingWords}</div>
        <div className="stat-label">REMAINING</div>
      </div>
      <div className="stat">
        <div className="stat-value">{totalWords}</div>
        <div className="stat-label">TOTAL</div>
      </div>
    </div>
  );
}

export default GameStats;
