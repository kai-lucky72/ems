import type { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

const Home: NextPage = () => {
  const router = useRouter();
  
  return (
    <div className="min-h-screen bg-gray-50">
      <Head>
        <title>Employee Management System</title>
        <meta name="description" content="A comprehensive employee management system" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
          <div className="flex items-center">
            <span className="text-blue-700 text-2xl font-bold">EMS</span>
          </div>
          <div className="flex space-x-4">
            <button onClick={() => router.push('/login')} className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded">
              Login
            </button>
            <button onClick={() => router.push('/register')} className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded">
              Register
            </button>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-16">
        <div className="flex flex-col items-center justify-center text-center">
          <h1 className="text-5xl font-bold mb-6">Employee Management System</h1>
          <p className="text-xl mb-10 max-w-3xl">
            A comprehensive solution for managing your company's workforce, departments, salaries, 
            leave requests and more - all in one platform.
          </p>
          
          <div className="flex flex-wrap justify-center gap-4 mb-16">
            <Link href="/login" className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg">
              Login
            </Link>
            <Link href="/register" className="bg-blue-800 hover:bg-blue-900 text-white font-semibold py-3 px-6 rounded-lg">
              Register
            </Link>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl">
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Employee Management</h3>
              <p className="text-gray-600 mb-4">
                Easily track employee information, status, roles, and departments.
              </p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Salary Processing</h3>
              <p className="text-gray-600 mb-4">
                Manage salaries, deductions, and payroll in a streamlined interface.
              </p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Leave Management</h3>
              <p className="text-gray-600 mb-4">
                Track leave requests, approvals, and employee availability.
              </p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Department Structure</h3>
              <p className="text-gray-600 mb-4">
                Organize and manage your company's departments and budgets.
              </p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Analytics Dashboard</h3>
              <p className="text-gray-600 mb-4">
                Get insights into workforce distribution, budget allocation, and more.
              </p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Messaging System</h3>
              <p className="text-gray-600 mb-4">
                Communicate with employees directly through the platform.
              </p>
            </div>
          </div>
        </div>
      </main>

      <footer className="bg-gray-800 text-white py-8 mt-auto">
        <div className="container mx-auto px-4 text-center">
          <p>&copy; {new Date().getFullYear()} Employee Management System. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default Home;