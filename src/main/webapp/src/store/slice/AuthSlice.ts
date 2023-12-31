import { createSlice } from '@reduxjs/toolkit'

export type AuthState = Partial<Record<string, Partial<Record<string, any>>>>
export const AuthSlice = createSlice({
  name: 'form',
  initialState: (): FormState => ({}),
  reducers: {
    saveForm: (state, action) => {
      const schemaName: string = action.payload.schemaName
      const subSchemaName: string = action.payload.subSchemaName
      if (!state[schemaName]) {
        state[schemaName] = {}
      }
      state[schemaName]![subSchemaName] = action.payload.form
    },
    clearForm: (state, action) => {
      const schemaName: string = action.payload.schemaName
      const subSchemaName: string = action.payload.subSchemaName
      delete state[schemaName]?.[subSchemaName]
    },
    clearAllForm: () => ({})
  }
})
