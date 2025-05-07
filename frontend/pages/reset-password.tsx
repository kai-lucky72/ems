import { useState, useEffect } from 'react';
import type { NextPage } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import axios from 'axios';
import Link from 'next/link';

const ResetPassword: NextPage = () => {
  const router = useRouter();
  const { token } = router.query;
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [step, setStep] = useState(token ? 'reset' : 'request');
  const [tokenValid, setTokenValid] = useState(true);

  useEffect(() => {
    // If token is provided, verify it's valid
    const verifyToken = async () => {
      if (!token) return;
      
      try {
        await axios.get(`/api/auth/verify-reset-token?token=${token}`);
        setTokenValid(true);
        setStep('reset');
      } catch (err) {
        console.error('Invalid or expired token:', err);
        setTokenValid(false);
        setStep('invalid');
        setError('This password reset link is invalid or has expired. Please request a new one.');
      }
    };

    if (token) {
      verifyToken();
    } else {
      setStep('request');
    }
  }, [token]);

  const handleRequestReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      await axios.post('/api/auth/reset-password-request', null, {
        params: { email }
      });
      
      setSuccess(true);
      setStep('check-email');
    } catch (err: any) {
      console.error('Password reset request error:', err);
      setError(err.response?.data?.message || 'Failed to request password reset. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!token) {
      setError('Missing reset token');
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
      await axios.post('/api/auth/reset-password', null, {
        params: {
          token: token as string,
          newPassword: password
        }
      });
      
      setSuccess(true);
      setStep('complete');
      
      // Redirect to login page after 3 seconds
      setTimeout(() => {
        router.push('/login?reset=true');
      }, 3000);
    } catch (err: any) {
      console.error('Password reset error:', err);
      setError(err.response?.data?.message || 'Failed to reset password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <Head>
        <title>Reset Password | Employee Management System</title>
      </Head>

      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          {step === 'request' ? 'Reset your password' : 
           step === 'check-email' ? 'Check your email' :
           step === 'reset' ? 'Set new password' :
           step === 'complete' ? 'Password reset complete' : 
           'Invalid reset link'}
        </h2>
        {step === 'request' && (
          <p className="mt-2 text-center text-sm text-gray-600">
            Enter your email and we'll send you a link to reset your password
          </p>
        )}
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}
          
          {step === 'request' && (
            <form className="space-y-6" onSubmit={handleRequestReset}>
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                  Email address
                </label>
                <div className="mt-1">
                  <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
              </div>

              <div>
                <button
                  type="submit"
                  disabled={loading}
                  className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                >
                  {loading ? 'Sending...' : 'Reset Password'}
                </button>
              </div>
            </form>
          )}
          
          {step === 'check-email' && (
            <div className="text-center">
              <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-4">
                If an account exists for {email}, we've sent a password reset link to that email address.
              </div>
              <p className="mt-2 text-gray-600">
                Please check your email for instructions to reset your password.
              </p>
            </div>
          )}
          
          {step === 'reset' && tokenValid && (
            <form className="space-y-6" onSubmit={handleResetPassword}>
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
                  {loading ? 'Updating...' : 'Update Password'}
                </button>
              </div>
            </form>
          )}
          
          {step === 'complete' && (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded">
              <p>Your password has been successfully reset!</p>
              <p className="mt-2">Redirecting to login page...</p>
            </div>
          )}
          
          {step === 'invalid' && (
            <div className="text-center">
              <p className="text-red-600 mb-4">Invalid or expired reset link.</p>
              <Link href="/reset-password" className="text-primary hover:text-primary-dark">
                Request a new password reset link
              </Link>
            </div>
          )}
          
          <div className="mt-6 text-center">
            <Link href="/login" className="text-sm text-primary hover:text-primary-dark">
              Back to login
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;