import { useState } from 'react'

// Set VITE_API_BASE in .env.production (or as a build-time env var) once the backend is deployed.
// Falls back to localhost for local dev.
const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

function App() {
  const [longUrl, setLongUrl] = useState('')
  const [shortUrl, setShortUrl] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setShortUrl('')

    if (!longUrl.trim()) {
      setError('Please enter a URL.')
      return
    }

    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/shorten`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ longUrl: longUrl.trim() }),
      })

      if (!res.ok) {
        throw new Error('Server returned an error.')
      }

      const data = await res.json()
      setShortUrl(data.shortUrl)
    } catch (err) {
      setError('Something went wrong. Please try again shortly.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-sm border border-gray-200 p-8">
        <h1 className="text-2xl font-semibold text-gray-900 mb-1">URL Shortener</h1>
        <p className="text-sm text-gray-500 mb-6">Paste a long URL and get a short one.</p>

        <form onSubmit={handleSubmit} className="flex gap-2">
          <input
            type="text"
            value={longUrl}
            onChange={(e) => setLongUrl(e.target.value)}
            placeholder="https://example.com/very/long/url"
            className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
          >
            {loading ? 'Shortening...' : 'Shorten'}
          </button>
        </form>

        {error && (
          <p className="mt-4 text-sm text-red-600">{error}</p>
        )}

        {shortUrl && (
          <div className="mt-6 bg-gray-50 border border-gray-200 rounded-lg p-4">
            <p className="text-xs text-gray-500 mb-1">Your short URL:</p>
            <a
              href={shortUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 font-medium break-all hover:underline"
            >
              {shortUrl}
            </a>
          </div>
        )}
      </div>
    </div>
  )
}

export default App
