import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // フロントからの /api へのアクセスを、バックエンド (localhost:8080) へ転送する
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        // 必要に応じてパスを書き換える場合はここに rewrite を書きます
        // 例: /api を外して送りたい場合
        // rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
})
