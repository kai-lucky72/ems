import { ReactNode, useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Head from 'next/head';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import { checkUserAuthentication } from '@/lib/auth';

interface LayoutProps {
  children: ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
  const router = useRouter();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      const authenticated = await checkUserAuthentication();
      setIsAuthenticated(authenticated);
      
      if (!authenticated) {
        router.push('/login');
      }
    };
    
    checkAuth();
  }, [router]);

  // Loading state while checking authentication
  if (isAuthenticated === null) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Head>
        <title>Dashboard | Employee Management System</title>
      </Head>
      
      <Navbar toggleSidebar={toggleSidebar} />
      
      <div className="flex">
        <Sidebar isOpen={isSidebarOpen} />
        
        <main className={`flex-1 p-6 transition-all duration-300 ${isSidebarOpen ? 'md:ml-64' : 'ml-0'}`}>
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;