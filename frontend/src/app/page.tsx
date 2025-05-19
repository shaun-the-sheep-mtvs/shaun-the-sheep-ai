import styles from "./page.module.css";

export default function Home() {
  return (
    <div className={styles.page}>
      <main className={styles.main}>
        <div className={styles.loginContainer}>
          <h1>Login</h1>
          <form className={styles.loginForm}>
            <div className={styles.formGroup}>
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email"
              />
            </div>
            <div className={styles.formGroup}>
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter your password"
              />
            </div>
            <button type="submit" className={styles.loginButton}>
              Sign In
            </button>
          </form>
        </div>
      </main>
    </div>
  );
}
