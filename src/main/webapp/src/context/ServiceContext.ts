import { AuthService } from '@service/SchemaService'
import React from 'react'

export interface ServiceContextType {
  schemaService?: AuthService
}

export const ServiceContext = React.createContext<ServiceContextType>({})
