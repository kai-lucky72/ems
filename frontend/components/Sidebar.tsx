import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { isManager, isEmployee } from '@/lib/auth';

interface SidebarProps {
  isOpen: boolean;
}

const Sidebar = ({ isOpen }: SidebarProps) => {
  const router = useRouter();
  const [userRole, setUserRole] = useState<'MANAGER' | 'EMPLOYEE' | null>(null);

  useEffect(() => {
    if (isManager()) {
      setUserRole('MANAGER');
    } else if (isEmployee()) {
      setUserRole('EMPLOYEE');
    }
  }, []);

  const isActive = (path: string) => {
    return router.pathname === path || router.pathname.startsWith(`${path}/`);
  };

  return (
    <aside 
      className={`fixed inset-y-0 left-0 z-40 w-64 bg-white shadow-md transform 
        ${isOpen ? 'translate-x-0' : '-translate-x-full'} 
        transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0 pt-16`}
    >
      <div className="h-full px-3 py-4 overflow-y-auto">
        <ul className="space-y-2">
          <li>
            <Link 
              href="/dashboard" 
              className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                isActive('/dashboard') && !isActive('/dashboard/departments') && !isActive('/dashboard/employees') ? 'bg-gray-100' : ''
              }`}
            >
              <svg 
                className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                fill="currentColor" 
                viewBox="0 0 20 20" 
                xmlns="http://www.w3.org/2000/svg"
              >
                <path d="M2 10a8 8 0 018-8v8h8a8 8 0 11-16 0z"></path>
                <path d="M12 2.252A8.014 8.014 0 0117.748 8H12V2.252z"></path>
              </svg>
              <span className="ml-3">Dashboard</span>
            </Link>
          </li>

          {/* Manager-only navigation items */}
          {userRole === 'MANAGER' && (
            <>
              <li>
                <Link 
                  href="/dashboard/departments" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/departments') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path d="M5 3a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2H5zM5 11a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2v-2a2 2 0 00-2-2H5zM11 5a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V5zM11 13a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
                  </svg>
                  <span className="ml-3">Departments</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/employees" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/employees') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd"></path>
                  </svg>
                  <span className="ml-3">Employees</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/salary" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/salary') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path fillRule="evenodd" d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd"></path>
                  </svg>
                  <span className="ml-3">Salary</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/leave" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/leave') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd"></path>
                  </svg>
                  <span className="ml-3">Leave Management</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/messaging" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/messaging') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path d="M2 5a2 2 0 012-2h7a2 2 0 012 2v4a2 2 0 01-2 2H9l-3 3v-3H4a2 2 0 01-2-2V5z"></path>
                    <path d="M15 7v2a4 4 0 01-4 4H9.828l-1.766 1.767c.28.149.599.233.938.233h2l3 3v-3h2a2 2 0 002-2V9a2 2 0 00-2-2h-1z"></path>
                  </svg>
                  <span className="ml-3">Messaging</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/analytics" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/analytics') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path d="M2 10a8 8 0 018-8v8h8a8 8 0 11-16 0z"></path>
                    <path d="M12 2.252A8.014 8.014 0 0117.748 8H12V2.252z"></path>
                  </svg>
                  <span className="ml-3">Analytics</span>
                </Link>
              </li>
            </>
          )}

          {/* Employee-only navigation items */}
          {userRole === 'EMPLOYEE' && (
            <>
              <li>
                <Link 
                  href="/dashboard/profile" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/profile') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd"></path>
                  </svg>
                  <span className="ml-3">My Profile</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/my-salary" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/my-salary') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path fillRule="evenodd" d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd"></path>
                  </svg>
                  <span className="ml-3">My Salary</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/my-leave" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/my-leave') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd"></path>
                  </svg>
                  <span className="ml-3">My Leave</span>
                </Link>
              </li>
              
              <li>
                <Link 
                  href="/dashboard/messages" 
                  className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                    isActive('/dashboard/messages') ? 'bg-gray-100' : ''
                  }`}
                >
                  <svg 
                    className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                    fill="currentColor" 
                    viewBox="0 0 20 20" 
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path d="M2 5a2 2 0 012-2h7a2 2 0 012 2v4a2 2 0 01-2 2H9l-3 3v-3H4a2 2 0 01-2-2V5z"></path>
                    <path d="M15 7v2a4 4 0 01-4 4H9.828l-1.766 1.767c.28.149.599.233.938.233h2l3 3v-3h2a2 2 0 002-2V9a2 2 0 00-2-2h-1z"></path>
                  </svg>
                  <span className="ml-3">Messages</span>
                </Link>
              </li>
            </>
          )}
          
          {/* Common navigation items for both roles */}
          <li>
            <Link 
              href="/dashboard/help" 
              className={`flex items-center p-2 text-gray-900 rounded-lg hover:bg-gray-100 group ${
                isActive('/dashboard/help') ? 'bg-gray-100' : ''
              }`}
            >
              <svg 
                className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-gray-900" 
                fill="currentColor" 
                viewBox="0 0 20 20" 
                xmlns="http://www.w3.org/2000/svg"
              >
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-3a1 1 0 00-.867.5 1 1 0 11-1.731-1A3 3 0 0113 8a3.001 3.001 0 01-2 2.83V11a1 1 0 11-2 0v-1a1 1 0 011-1 1 1 0 100-2zm0 8a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd"></path>
              </svg>
              <span className="ml-3">Help & Support</span>
            </Link>
          </li>
        </ul>
      </div>
    </aside>
  );
};

export default Sidebar;