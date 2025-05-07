import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { getUser } from '@/lib/auth';
import { User } from '@/types';

const ProfilePage: NextPage = () => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
  });

  useEffect(() => {
    // Fetch user data
    const currentUser = getUser();
    if (currentUser) {
      setUser(currentUser);
      setFormData({
        fullName: currentUser.fullName,
        email: currentUser.email,
        phoneNumber: currentUser.phoneNumber || '',
      });
    }
    setLoading(false);
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      // Here we would normally make an API call to update the profile
      // For now, we'll just simulate success
      setTimeout(() => {
        // Update the user state with new values
        if (user) {
          const updatedUser = {
            ...user,
            fullName: formData.fullName,
            phoneNumber: formData.phoneNumber,
          };
          
          setUser(updatedUser);
          // In a real application, we would also update the user in localStorage
          localStorage.setItem('user', JSON.stringify(updatedUser));
          
          setEditMode(false);
          setLoading(false);
        }
      }, 500);
    } catch (error) {
      console.error('Failed to update profile:', error);
      setLoading(false);
    }
  };

  return (
    <ProtectedRoute>
      <Layout>
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold">My Profile</h1>
            {!editMode && (
              <button
                onClick={() => setEditMode(true)}
                className="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
              >
                Edit Profile
              </button>
            )}
          </div>
          
          {loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : !user ? (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              Failed to load user profile. Please try again later.
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow overflow-hidden">
              {editMode ? (
                // Edit mode form
                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                  <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                    <div className="sm:col-span-3">
                      <label htmlFor="fullName" className="block text-sm font-medium text-gray-700">
                        Full Name
                      </label>
                      <div className="mt-1">
                        <input
                          type="text"
                          name="fullName"
                          id="fullName"
                          required
                          autoComplete="name"
                          value={formData.fullName}
                          onChange={handleInputChange}
                          className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
                        />
                      </div>
                    </div>

                    <div className="sm:col-span-3">
                      <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                        Email Address
                      </label>
                      <div className="mt-1">
                        <input
                          type="email"
                          name="email"
                          id="email"
                          required
                          autoComplete="email"
                          value={formData.email}
                          disabled
                          className="bg-gray-100 shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
                        />
                        <p className="mt-1 text-sm text-gray-500">Email cannot be changed</p>
                      </div>
                    </div>

                    <div className="sm:col-span-3">
                      <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700">
                        Phone Number
                      </label>
                      <div className="mt-1">
                        <input
                          type="tel"
                          name="phoneNumber"
                          id="phoneNumber"
                          autoComplete="tel"
                          value={formData.phoneNumber}
                          onChange={handleInputChange}
                          className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="flex justify-end space-x-3">
                    <button
                      type="button"
                      onClick={() => setEditMode(false)}
                      className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      disabled={loading}
                      className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                    >
                      {loading ? 'Saving...' : 'Save Changes'}
                    </button>
                  </div>
                </form>
              ) : (
                // View mode display
                <div className="p-6 space-y-6">
                  <div>
                    <h3 className="text-lg leading-6 font-medium text-gray-900">Personal Information</h3>
                    <p className="mt-1 max-w-2xl text-sm text-gray-500">
                      Your personal information and account details.
                    </p>
                  </div>
                  
                  <div className="border-t border-gray-200 pt-5">
                    <dl className="divide-y divide-gray-200">
                      <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4">
                        <dt className="text-sm font-medium text-gray-500">Full name</dt>
                        <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{user.fullName}</dd>
                      </div>
                      <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4">
                        <dt className="text-sm font-medium text-gray-500">Email address</dt>
                        <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{user.email}</dd>
                      </div>
                      <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4">
                        <dt className="text-sm font-medium text-gray-500">Phone number</dt>
                        <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                          {user.phoneNumber || 'Not provided'}
                        </dd>
                      </div>
                      <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4">
                        <dt className="text-sm font-medium text-gray-500">Company</dt>
                        <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                          {user.companyName}
                        </dd>
                      </div>
                      <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4">
                        <dt className="text-sm font-medium text-gray-500">Account created</dt>
                        <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                          {/* We'll use a placeholder date since the User type doesn't include creation date */}
                          January 15, 2023
                        </dd>
                      </div>
                    </dl>
                  </div>
                </div>
              )}
            </div>
          )}
          
          {/* Security Section */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="p-6 space-y-6">
              <div>
                <h3 className="text-lg leading-6 font-medium text-gray-900">Security</h3>
                <p className="mt-1 max-w-2xl text-sm text-gray-500">
                  Manage your password and account security settings.
                </p>
              </div>
              
              <div className="border-t border-gray-200 pt-5">
                <div className="flex justify-between items-center">
                  <div>
                    <h4 className="text-base font-medium text-gray-900">Password</h4>
                    <p className="text-sm text-gray-500">
                      Last changed on January 15, 2023
                    </p>
                  </div>
                  <button
                    className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                    onClick={() => window.location.href = '/reset-password'}
                  >
                    Change Password
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default ProfilePage;