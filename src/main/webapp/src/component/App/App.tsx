import React, { FC, useEffect, useState } from 'react'
import { Outlet } from 'react-router-dom'
import * as config from '@config/config'
import { AuthService } from '@service/AuthService'
import { useAppDispatch } from '@store/Store'
import { ServiceContext } from '@context/ServiceContext'

// TODO: Update so config can be updated and overriden
const App: FC = () => {
  const [configLoadingState, setConfigLoadingState] = useState<"loading" | "ready" | "error">("loading");
  useEffect(() => {
    fetch(config.dynamicConfigUrl)
      .then(resp => setConfigLoadingState("ready"))
      .catch(err => setConfigLoadingState("error"))
  });
  // Initialize all the required services
  const schemaService = new AuthService(config.API_URL)
  // Get all schemas for the first time
  useAppDispatch()(async (dispatch, getState) => {
    const state = getState()
    if (!state.schemas.init) {
      Object.entries(await schemaService.getAllSchemas())
        .flatMap((entry) => entry.values)
        .forEach((schema) => dispatch(SchemaSlice.actions.saveSchema(schema)))
      dispatch(SchemaSlice.actions.markInit)
    }
  })
  return (
    <ServiceContext.Provider
      value={{
        schemaService: schemaService
      }}
    >
      <div>
        <Outlet />
      </div>
    </ServiceContext.Provider>
  )
}

export default App
