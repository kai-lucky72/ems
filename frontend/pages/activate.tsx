import { useState, useEffect } from 'react';
import type { NextPage } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import axios from 'axios';
import Link from 'next/link';

const ActivateAccount: NextPage = () => {
  const router = useRouter();
  const { token } = router.query;
  
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [tokenValid, setTokenValid] = useState(true);

  useEffect(() => {
    // Verify token is valid when component mounts
    const verifyToken = async () => {
      if (!token) return;
      
      try {
        await axios.get(`/api/auth/verify-activation-token?token=${token}`);
        setTokenValid(true);
      } catch (err) {
        console.error('Invalid or expired token:', err);
        setTokenValid(false);
        setError('This activation link is invalid or has expired. Please contact your manager for a new invitation.');
      }
    };

    if (token) {
      verifyToken();
    }
  }, [token]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!token) {
      setError('Missing activation token');
      return;
    }
    
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    if (password.length < 8) {
      setError('Password must be at least 8 characters long');
      return;
    }
    
    setLoading(true);
    setError('');
    
    try {
      await axios.post('/api/auth/activate', {
        token,
        password
      });
      
      setSuccess(true);
      // Redirect to login page after 3 seconds
      setTimeout(() => {
        router.push('/login?activated=true');
      }, 3000);
    } catch (err: any) {
      console.error('Activation error:', err);
      setError(err.response?.data?.message || 'Failed to activate account. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <Head>
        <title>Activate Account | Employee Management System</title>
      </Head>

      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Activate Your Account
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          Set your password to complete your account activation
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {success ? (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded">
              <p>Your account has been successfully activated!</p>
              <p className="mt-2">Redirecting to login page...</p>
            </div>
          ) : error ? (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          ) : !token ? (
            <div className="text-center">
              <p className="text-amber-600 mb-4">No activation token provided.</p>
              <p>Please use the link from your invitation email.</p>
            </div>
          ) : !tokenValid ? (
            <div className="text-center">
              <p className="text-red-600 mb-4">Invalid or expired activation link.</p>
              <p>Please contact your manager for a new invitation.</p>
            </div>
          ) : (
            <form className="space-y-6" onSubmit={handleSubmit}>
              <div>
                <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                  New Password
                </label>
                <div className="mt-1">
                  <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="new-password"
                    required
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </div>
              </div>

              <div>
                <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
                  Confirm Password
                </label>
                <div className="mt-1">
                  <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    autoComplete="new-password"
                    required
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                  />
                </div>
              </div>

              <div>
                <button
                  type="submit"
                  disabled={loading}
                  className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                >
                  {loading ? 'Activating...' : 'Activate Account'}
                </button>
              </div>
            </form>
          )}
          
          <div className="mt-6 text-center">
            <Link href="/login" className="text-sm text-primary hover:text-primary-dark">
              Return to login
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ActivateAccount;