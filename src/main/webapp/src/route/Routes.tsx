import App from '@component/App/App'
import Form from '@page/Form/Form'
import React from 'react'
import { RouteObject, useParams } from 'react-router-dom'

const Routes: RouteObject[] = [
  {
    path: '/',
    Component: App,
    children: [
      {
        path: '/forms/:schemaName/:subSchemaName',
        Component: () => {
          const params = useParams()
          return <Form schemaName={params.schemaName!} subSchemaName={params.subSchemaName!}></Form>
        }
      }
    ]
  }
]

export default Routes
