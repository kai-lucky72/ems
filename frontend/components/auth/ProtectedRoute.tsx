import { useEffect, useState, ReactNode } from 'react';
import { useRouter } from 'next/router';
import { checkUserAuthentication, isManager, isEmployee } from '@/lib/auth';

interface ProtectedRouteProps {
  children: ReactNode;
  requiredRole?: 'MANAGER' | 'EMPLOYEE' | 'ANY';
}

const ProtectedRoute = ({ children, requiredRole = 'ANY' }: ProtectedRouteProps) => {
  const router = useRouter();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const isAuthenticated = await checkUserAuthentication();
        
        if (!isAuthenticated) {
          router.push('/login?redirect=' + encodeURIComponent(router.asPath));
          return;
        }
        
        // Check role-based access
        let hasAccess = true;
        
        if (requiredRole === 'MANAGER' && !isManager()) {
          hasAccess = false;
        } else if (requiredRole === 'EMPLOYEE' && !isEmployee()) {
          hasAccess = false;
        }
        
        if (!hasAccess) {
          router.push('/dashboard');
        } else {
          setLoading(false);
        }
        
      } catch (error) {
        console.error('Authentication check failed:', error);
        router.push('/login');
      }
    };

    if (router.isReady) {
      checkAuth();
    }
  }, [router, requiredRole]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  return <>{children}</>;
};

export default ProtectedRoute;