import type { NextPage } from 'next';
import Head from 'next/head';
import Link from 'next/link';

const Home: NextPage = () => {
  return (
    <div className="min-h-screen bg-gray-50">
      <Head>
        <title>Employee Management System</title>
        <meta name="description" content="A comprehensive employee management system" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main className="container mx-auto px-4 py-16">
        <div className="flex flex-col items-center justify-center text-center">
          <h1 className="text-5xl font-bold mb-6">Employee Management System</h1>
          <p className="text-xl mb-10 max-w-3xl">
            A comprehensive solution for managing your company's workforce, departments, salaries, 
            leave requests and more - all in one platform.
          </p>
          
          <div className="flex flex-wrap justify-center gap-4 mb-16">
            <Link href="/login" className="btn btn-primary">
              Login
            </Link>
            <Link href="/register" className="btn btn-secondary">
              Register
            </Link>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl">
            <div className="card">
              <h3 className="text-xl font-semibold mb-4">Employee Management</h3>
              <p className="text-gray-600 mb-4">
                Easily track employee information, status, roles, and departments.
              </p>
            </div>
            
            <div className="card">
              <h3 className="text-xl font-semibold mb-4">Salary Processing</h3>
              <p className="text-gray-600 mb-4">
                Manage salaries, deductions, and payroll in a streamlined interface.
              </p>
            </div>
            
            <div className="card">
              <h3 className="text-xl font-semibold mb-4">Leave Management</h3>
              <p className="text-gray-600 mb-4">
                Track leave requests, approvals, and employee availability.
              </p>
            </div>
            
            <div className="card">
              <h3 className="text-xl font-semibold mb-4">Department Structure</h3>
              <p className="text-gray-600 mb-4">
                Organize and manage your company's departments and budgets.
              </p>
            </div>
            
            <div className="card">
              <h3 className="text-xl font-semibold mb-4">Analytics Dashboard</h3>
              <p className="text-gray-600 mb-4">
                Get insights into workforce distribution, budget allocation, and more.
              </p>
            </div>
            
            <div className="card">
              <h3 className="text-xl font-semibold mb-4">Messaging System</h3>
              <p className="text-gray-600 mb-4">
                Communicate with employees directly through the platform.
              </p>
            </div>
          </div>
        </div>
      </main>

      <footer className="bg-gray-800 text-white py-8">
        <div className="container mx-auto px-4 text-center">
          <p>&copy; {new Date().getFullYear()} Employee Management System. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default Home;