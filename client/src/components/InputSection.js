import React from 'react';

function InputSection({ guessInput, loading, remainingWords, onReset, onSubmit }) {
  return (
    <div className="input-section">
      <div className="input-display">
        {guessInput.toUpperCase() || <span className="placeholder">CLICK LETTERS ABOVE</span>}
      </div>
      <div className="control-buttons">
        <button 
          onClick={onReset}
          disabled={!guessInput || loading}
          className="btn btn-reset"
        >
          RESET
        </button>
        <button 
          onClick={onSubmit}
          disabled={loading || !guessInput.trim() || remainingWords === 0}
          className="btn btn-enter"
        >
          ENTER
        </button>
      </div>
    </div>
  );
}

export default InputSection;
