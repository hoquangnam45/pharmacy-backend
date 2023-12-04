// References: https://profinit.eu/en/blog/build-once-deploy-many-in-react-dynamic-configuration-properties/
export interface DynamicConfig {
    apiUrl: string;
    environment: "LOCAL" | "DEV" | "PROD";
}
  
export const defaultConfig: DynamicConfig = {
    apiUrl: "https://localhost:8080/api",
    environment: "LOCAL"
}

export const dynamicConfigUrl = "config.json";