import { useState } from "react";

const orderApiUrl = import.meta.env.VITE_ORDER_API_URL || "/api/order";
const userApiBaseUrl = import.meta.env.VITE_USER_API_URL || "/user-api/users";

const initialAuthForm = {
  name: "",
  email: "",
  password: ""
};

export default function App() {
  const [mode, setMode] = useState("login");
  const [authForm, setAuthForm] = useState(initialAuthForm);
  const [authLoading, setAuthLoading] = useState(false);
  const [authError, setAuthError] = useState("");
  const [authMessage, setAuthMessage] = useState("");
  const [currentUser, setCurrentUser] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [productId, setProductId] = useState("101");

  const handleAuthChange = (event) => {
    const { name, value } = event.target;
    setAuthForm((currentValue) => ({
      ...currentValue,
      [name]: value
    }));
  };

  const handleAuthSubmit = async (event) => {
    event.preventDefault();
    setAuthLoading(true);
    setAuthError("");
    setAuthMessage("");

    try {
      const endpoint = mode === "signup" ? `${userApiBaseUrl}/signup` : `${userApiBaseUrl}/login`;
      const payload =
        mode === "signup"
          ? authForm
          : {
              email: authForm.email,
              password: authForm.password
            };

      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Authentication request failed");
      }

      setCurrentUser(data);
      setAuthMessage(data.message);
      setAuthForm(initialAuthForm);
    } catch (requestError) {
      setAuthError(requestError.message);
    } finally {
      setAuthLoading(false);
    }
  };

  const placeOrder = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch(orderApiUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          userId: currentUser?.id,
          productId: Number(productId)
        })
      });

      if (!response.ok) {
        throw new Error("Order request failed");
      }

      const data = await response.json();
      setResult(data);
    } catch (requestError) {
      setError(requestError.message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="app-shell">
      <div className="panel-grid">
        <section className="card auth-card">
          <div className="section-label">User Access</div>
          <h1>{mode === "signup" ? "Create Account" : "Welcome Back"}</h1>
          <p>
            {mode === "signup"
              ? "Create a user profile and store it in PostgreSQL."
              : "Log in with a user already stored in PostgreSQL."}
          </p>

          <div className="mode-switch">
            <button
              type="button"
              className={mode === "login" ? "tab active" : "tab"}
              onClick={() => {
                setMode("login");
                setAuthError("");
                setAuthMessage("");
              }}
            >
              Login
            </button>
            <button
              type="button"
              className={mode === "signup" ? "tab active" : "tab"}
              onClick={() => {
                setMode("signup");
                setAuthError("");
                setAuthMessage("");
              }}
            >
              Sign Up
            </button>
          </div>

          <form className="auth-form" onSubmit={handleAuthSubmit}>
            {mode === "signup" && (
              <label>
                Name
                <input
                  name="name"
                  value={authForm.name}
                  onChange={handleAuthChange}
                  placeholder="Enter your full name"
                  required
                />
              </label>
            )}

            <label>
              Email
              <input
                type="email"
                name="email"
                value={authForm.email}
                onChange={handleAuthChange}
                placeholder="Enter your email"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                name="password"
                value={authForm.password}
                onChange={handleAuthChange}
                placeholder="Enter your password"
                required
              />
            </label>

            <button type="submit" disabled={authLoading}>
              {authLoading
                ? mode === "signup"
                  ? "Creating Account..."
                  : "Signing In..."
                : mode === "signup"
                  ? "Create Account"
                  : "Login"}
            </button>
          </form>

          {authError && <p className="error">{authError}</p>}
          {authMessage && <p className="success">{authMessage}</p>}

          {currentUser && (
            <div className="user-pill">
              <span>Logged in as</span>
              <strong>{currentUser.name}</strong>
              <small>{currentUser.email}</small>
            </div>
          )}
        </section>

        <section className="card order-card">
          <div className="section-label">Order Flow</div>
          <h2>Place Order</h2>
          <p>Use the signed-in user and place a product order through the microservice flow.</p>

          <label>
            Product ID
            <input
              value={productId}
              onChange={(event) => setProductId(event.target.value)}
              placeholder="101"
            />
          </label>

          <button onClick={placeOrder} disabled={loading || !currentUser}>
            {loading ? "Placing Order..." : "Place Order"}
          </button>

          {!currentUser && <p className="helper">Login or sign up first to place an order.</p>}
          {error && <p className="error">{error}</p>}

          {result && <pre>{JSON.stringify(result, null, 2)}</pre>}
        </section>
      </div>
    </main>
  );
}
