
// the purpose of this file is to create a UserContext 
// that can be used to manage user state across the application.
import React, { createContext, useState } from 'react';

export const UserContext = createContext(null);

export const UserProvider = ({ children }) => {
  // User is an object (BUUUUURRRN)
  //for now the user state is not important, but it will be used to store user information
  const [user, setUser] = useState(null);

  return (
    // UserContext.Provider is a component that provides the user state to its children
    // the children in this case is the entire application
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
};
