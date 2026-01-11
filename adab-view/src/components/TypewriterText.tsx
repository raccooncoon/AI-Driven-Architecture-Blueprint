import React, { useState, useEffect } from 'react';

interface TypewriterTextProps {
  text: string;
  speed?: number;
  delay?: number;
}

export const TypewriterText: React.FC<TypewriterTextProps> = ({ 
  text, 
  speed = 20, 
  delay = 0 
}) => {
  const [displayedText, setDisplayedText] = useState('');
  const [isTyping, setIsTyping] = useState(false);

  useEffect(() => {
    let timeoutId: ReturnType<typeof setTimeout> | undefined;
    let index = 0;

    const startTyping = () => {
      setIsTyping(true);
      const timer = setInterval(() => {
        if (index <= text.length) {
          setDisplayedText(text.slice(0, index));
          index++;
        } else {
          clearInterval(timer);
          setIsTyping(false);
        }
      }, speed);
      return () => clearInterval(timer);
    };

    if (delay > 0) {
      timeoutId = setTimeout(startTyping, delay);
    } else {
      startTyping();
    }

    return () => {
      if (timeoutId) clearTimeout(timeoutId);
    };
  }, [text, speed, delay]);

  return (
    <span style={{ position: 'relative' }}>
      {displayedText}
      {isTyping && (
        <span style={{
          display: 'inline-block',
          width: '2px',
          height: '1em',
          background: '#60a5fa',
          marginLeft: '2px',
          verticalAlign: 'middle',
          animation: 'cursorBlink 0.8s infinite'
        }} />
      )}
      <style>{`
        @keyframes cursorBlink {
          0%, 100% { opacity: 1; }
          50% { opacity: 0; }
        }
      `}</style>
    </span>
  );
};
