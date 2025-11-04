import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface Notification {
  message: string;
  createdAt: string;
}

interface Props {
  userId: number;
}

function NotificationPopup({ userId }: Props) {
  const [latestNotification, setLatestNotification] = useState<Notification | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!userId) {
      console.log('⏳ Waiting for userId...');
      return;
    }

    console.log('🔌 Connecting SSE for user:', userId);

    const eventSource = new EventSource(`http://localhost:8080/notifications/stream/${userId}`);

    eventSource.onopen = () => {
      console.log('✅ SSE Connected for user:', userId);
    };

    eventSource.addEventListener('notification', (event) => {
      console.log('📬 Notification received:', event.data);
      const data = JSON.parse(event.data);
      setLatestNotification(data);

      setTimeout(() => setLatestNotification(null), 5000);
    });

    eventSource.onerror = (err) => {
      console.error('❌ SSE connection error:', err);
      if (eventSource.readyState === EventSource.CLOSED) {
        console.error('Connection closed by server');
      } else if (eventSource.readyState === EventSource.CONNECTING) {
        console.log('Reconnecting...');
      }
      eventSource.close();
    };

    return () => {
      console.log('🔌 Closing SSE connection');
      eventSource.close();
    };
  }, [userId]);

  if (!latestNotification) return null;

  return (
    <div
      className="notification-popup"
      onClick={() => navigate("/notifications")}
      style={{
        position: 'fixed',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        backgroundColor: 'rgba(0, 0, 0, 0.85)',
        color: 'white',
        padding: '20px 30px',
        borderRadius: '12px',
        boxShadow: '0 4px 20px rgba(0, 0, 0, 0.4)',
        zIndex: 999999,
        textAlign: 'center',
        fontSize: '16px',
        cursor: 'pointer',
        transition: 'opacity 0.3s ease, transform 0.3s ease',
      }}
    >
      <p style={{ margin: 0 }}>{latestNotification.message}</p>
      <small style={{ opacity: 0.8 }}>
        {new Date(latestNotification.createdAt).toLocaleTimeString()}
      </small>
    </div>
  );
}

export default NotificationPopup;
