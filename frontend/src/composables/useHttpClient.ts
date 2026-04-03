export class ApiError extends Error {
	public status: number;
	public responseBody: any;

	constructor(message: string, status: number, responseBody: any = null) {
		super(message);
		this.name = "ApiError";
		this.status = status;
		this.responseBody = responseBody;

		Object.setPrototypeOf(this, ApiError.prototype);
	}
}

export type RequestOptions = Omit<RequestInit, "method" | "body"> & {
	body?: any;
};

export function useHttpClient() {
	const MAX_RETRIES = 3;
	const SERVER_HOST = "http://localhost:8080";

	async function request<T = void>(
		method: string,
		url: string,
		options: RequestOptions = {},
		retryCount = 0
	): Promise<T> {
		const apiUrl = url.startsWith("/") ? `/api${url}` : `/api/${url}`;

		async function doFetch(): Promise<T> {
			const fetchOptions: RequestInit = {
				method,
				credentials: "include",
				headers: {
					"Content-Type": "application/json",
					"Accept": "application/json",
					...(options.headers || {}),
				},
				body: options.body ? JSON.stringify(options.body) : undefined,
			};

			const response = await fetch(`${SERVER_HOST}${apiUrl}`, fetchOptions);

			if (response.status === 401) {
				if (retryCount >= MAX_RETRIES) {
					window.location.href = "/";
					throw new Error("Too many unauthorized retries");
				}

				const refreshed = await refreshToken();
				if (refreshed) {
					return request<T>(method, url, options, retryCount + 1);
				}
				else {
					window.location.href = "/";
					throw new Error("Refresh failed");
				}
			}

			if (!response.ok) {
				let responseBody: any = null;
				let message = `HTTP error ${response.status}`;

				try {
					responseBody = await response.json();
					if (responseBody && responseBody.message) {
						message = responseBody.message;
					}
					else if (responseBody && responseBody.detail) {
						message = responseBody.detail;
					}
				}
				catch {
					responseBody = await response.text();
				}

				throw new ApiError(message, response.status, responseBody);
			}

			const text = await response.text();

			if (response.status === 204 || !text) {
				return undefined as unknown as T;
			}

			try {
				return JSON.parse(text) as T;
			}
			catch {
				throw new Error("Invalid JSON response");
			}
		}

		return doFetch();
	}

	async function refreshToken(): Promise<boolean> {
		try {
			const response = await fetch(`${SERVER_HOST}/api/auth/extend-login`, {
				method: "POST",
				credentials: "include",
				headers: {
					"Content-Type": "application/json",
					"Accept": "application/json",
				},
			});

			return response.ok;
		}
		catch {
			return false;
		}
	}

	async function httpGet<T = void>(url: string, options?: RequestOptions): Promise<T> {
		return request<T>("GET", url, options);
	}

	async function httpPost<T = void>(url: string, body?: any, options?: RequestOptions): Promise<T> {
		return request<T>("POST", url, {...options, body});
	}

	async function httpPut<T = void>(url: string, body?: any, options?: RequestOptions): Promise<T> {
		return request<T>("PUT", url, {...options, body});
	}

	async function httpPatch<T = void>(url: string, body?: any, options?: RequestOptions): Promise<T> {
		return request<T>("PATCH", url, {...options, body});
	}

	async function httpDelete<T = void>(url: string, body?: any, options?: RequestOptions): Promise<T> {
		return request<T>("DELETE", url, {...options, body});
	}

	return {
		httpGet,
		httpPost,
		httpPut,
		httpPatch,
		httpDelete,
	};
}
