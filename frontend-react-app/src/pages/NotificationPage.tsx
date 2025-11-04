import { useEffect, useState } from "react";
import { getNotifications } from "../api/notificationApi";
import "../styles/NotificationPage.css";

interface Notification {
  message: string;
  createdAt: string;
}

function NotificationPage() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const userId = Number(sessionStorage.getItem("userId")); // Replace with logged-in user ID from auth context

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {
    try {
      const data = await getNotifications(userId);
      setNotifications(data.notifications);
    } catch (err) {
      console.error("Error fetching notifications:", err);
    }
  };

  return (
    <div className="notifications-container">
      <h2 className="notifications-title">🔔 Your Notifications</h2>
      {notifications.length === 0 ? (
        <p className="no-notifications">No notifications yet.</p>
      ) : (
        <ul className="notification-list">
          {notifications.map((n) => (
            <li className="notification-item">
              <div className="notification-message">{n.message}</div>
              <div className="notification-date">
                {new Date(n.createdAt).toLocaleString()}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default NotificationPage;
