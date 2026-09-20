import React, { useState, useEffect, useRef } from 'react';
import './Chatbot.css';

const Chatbot = () => {
  const [isActive, setIsActive] = useState(false);
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const [showTyping, setShowTyping] = useState(false);
  const chatBodyRef = useRef(null);

  // Initialize with welcome message
  useEffect(() => {
    const initialMessage = {
      id: 1,
      type: 'bot',
      content: 'Xin chào! 👋 Tôi là Trợ lý AI. Tôi có thể giúp gì cho bạn hôm nay?',
      time: getCurrentTime(),
      showQuickChips: true
    };
    setMessages([initialMessage]);
  }, []);

  // Scroll to bottom when messages change
  useEffect(() => {
    if (chatBodyRef.current) {
      chatBodyRef.current.scrollTop = chatBodyRef.current.scrollHeight;
    }
  }, [messages, showTyping]);

  const toggleChat = () => {
    setIsActive(!isActive);
  };

  const clearChat = () => {
    const resetMessage = {
      id: Date.now(),
      type: 'bot',
      content: 'Lịch sử hội thoại đã được làm mới! 👋 Tôi có thể giúp gì cho bạn?',
      time: getCurrentTime(),
      showQuickChips: true
    };
    setMessages([resetMessage]);
  };

  const handleSendMessage = (text) => {
    if (!text.trim()) return;

    // Add user message
    const userMessage = {
      id: Date.now(),
      type: 'user',
      content: text,
      time: getCurrentTime()
    };
    setMessages(prev => [...prev, userMessage]);
    setInputValue('');

    // Show typing indicator
    setShowTyping(true);

    // Simulate bot response
    setTimeout(() => {
      setShowTyping(false);
      const botReply = generateBotReply(text);
      const botMessage = {
        id: Date.now() + 1,
        type: 'bot',
        content: botReply,
        time: getCurrentTime()
      };
      setMessages(prev => [...prev, botMessage]);
    }, 1200);
  };

  const handleQuickMessage = (text) => {
    setInputValue(text);
    handleSendMessage(text);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSendMessage(inputValue);
    }
  };

  const getCurrentTime = () => {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  };

  const escapeHTML = (str) => {
    return str.replace(/[&<>'"]/g, 
      tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
  };

  const generateBotReply = (userInput) => {
    const input = userInput.toLowerCase();

    if (input.includes('sản phẩm') || input.includes('nổi bật')) {
      return 'Các sản phẩm nổi bật nhất tuần này bao gồm:<br>• <b>Laptop Bacon Pro 16"</b><br>• <b>Smartphone X100</b><br>• <b>Tai nghe chống ồn Pro</b>';
    } else if (input.includes('đổi trả') || input.includes('bảo hành')) {
      return 'Chính sách bảo hành & đổi trả:<br>1. Đổi mới trong <b>30 ngày</b> nếu có lỗi nhà sản xuất.<br>2. Bảo hành chính hãng <b>12-24 tháng</b>.';
    } else if (input.includes('cấu hình') || input.includes('tư vấn')) {
      return 'Bạn đang tìm kiếm cấu hình cho nhu cầu công việc gì? (Ví dụ: <i>Đồ họa, Lập trình, hay Văn phòng nhẹ nhàng?</i>)';
    } else if (input.includes('chào') || input.includes('hello') || input.includes('hi')) {
      return 'Chào bạn! Chúc bạn một ngày tốt lành. Bạn cần hỗ trợ thông tin gì ạ?';
    } else {
      return 'Cảm ơn bạn đã nhắn tin! Yêu cầu của bạn đã được ghi nhận. Chuyên viên chăm sóc khách hàng sẽ phản hồi ngay lập tức.';
    }
  };

  return (
    <div className={`chatbot-widget ${isActive ? 'active' : ''}`}>
      {/* Floating Toggle Button */}
      <button 
        className="chatbot-toggle" 
        onClick={toggleChat}
        aria-label="Toggle Chat"
      >
        <div className="pulse-ring"></div>
        
        {/* Chat Icon with stars */}
        <div className="icon-chat">
          <i className="fa-solid fa-comments"></i>
          <svg className="star-sparkle star-1" viewBox="0 0 24 24">
            <path d="M12 0L14.59 9.41L24 12L14.59 14.59L12 24L9.41 14.59L0 12L9.41 9.41L12 0Z"/>
          </svg>
          <svg className="star-sparkle star-2" viewBox="0 0 24 24">
            <path d="M12 0L14.59 9.41L24 12L14.59 14.59L12 24L9.41 14.59L0 12L9.41 9.41L12 0Z"/>
          </svg>
          <svg className="star-sparkle star-3" viewBox="0 0 24 24">
            <path d="M12 0L14.59 9.41L24 12L14.59 14.59L12 24L9.41 14.59L0 12L9.41 9.41L12 0Z"/>
          </svg>
        </div>

        <i className="fa-solid fa-xmark icon-close"></i>
        <span className="notification-badge">1</span>
      </button>

      {/* Expandable Chat Window */}
      <div className="chat-window">
        {/* Header */}
        <div className="chat-header">
          <div className="bot-info">
            <div className="avatar-container">
              <img 
                src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=120&q=80" 
                alt="AI Avatar" 
                className="avatar-img"
              />
              <span className="status-dot"></span>
            </div>
            <div className="bot-details">
              <h3>AI Assistant ✨</h3>
              <p>
                <i className="fa-solid fa-circle" style={{fontSize: '6px', color: '#4ade80'}}></i> 
                Sẵn sàng hỗ trợ
              </p>
            </div>
          </div>
          <div className="header-actions">
            <button 
              className="header-btn" 
              onClick={clearChat}
              title="Xóa lịch sử chat"
            >
              <i className="fa-solid fa-rotate-right"></i>
            </button>
            <button 
              className="header-btn" 
              onClick={toggleChat}
              title="Đóng"
            >
              <i className="fa-solid fa-chevron-down"></i>
            </button>
          </div>
        </div>

        {/* Scrollable Message Body */}
        <div className="chat-body" ref={chatBodyRef}>
          {messages.map((msg) => (
            <div key={msg.id} className={`message-row ${msg.type}`}>
              {msg.type === 'bot' && (
                <div className="msg-avatar">
                  <i className="fa-solid fa-robot"></i>
                </div>
              )}
              <div className="msg-content">
                <div 
                  className="msg-bubble"
                  dangerouslySetInnerHTML={{ __html: msg.content }}
                />
                {msg.showQuickChips && (
                  <div className="quick-chips">
                    <button 
                      className="chip-btn"
                      onClick={() => handleQuickMessage('Sản phẩm nổi bật')}
                    >
                      🔥 Sản phẩm nổi bật
                    </button>
                    <button 
                      className="chip-btn"
                      onClick={() => handleQuickMessage('Chính sách đổi trả')}
                    >
                      📦 Chính sách đổi trả
                    </button>
                    <button 
                      className="chip-btn"
                      onClick={() => handleQuickMessage('Tư vấn cấu hình')}
                    >
                      💻 Tư vấn cấu hình
                    </button>
                  </div>
                )}
                <span className="msg-time">{msg.time}</span>
              </div>
            </div>
          ))}

          {/* Typing Indicator */}
          {showTyping && (
            <div className="message-row bot">
              <div className="msg-avatar">
                <i className="fa-solid fa-robot"></i>
              </div>
              <div className="typing-indicator">
                <div className="typing-dot"></div>
                <div className="typing-dot"></div>
                <div className="typing-dot"></div>
              </div>
            </div>
          )}
        </div>

        {/* Footer / Input Controls */}
        <div className="chat-footer">
          <div className="input-wrapper">
            <input 
              type="text" 
              className="chat-input"
              placeholder="Nhập tin nhắn..."
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyPress={handleKeyPress}
              autoComplete="off"
            />
            <button 
              className="attach-btn" 
              title="Đính kèm tệp"
              onClick={() => alert('Tính năng tải tệp đang phát triển!')}
            >
              <i className="fa-solid fa-paperclip"></i>
            </button>
          </div>
          <button 
            className="send-btn"
            onClick={() => handleSendMessage(inputValue)}
            title="Gửi tin nhắn"
            disabled={!inputValue.trim()}
          >
            <i className="fa-solid fa-paper-plane"></i>
          </button>
        </div>
      </div>
    </div>
  );
};

export default Chatbot;