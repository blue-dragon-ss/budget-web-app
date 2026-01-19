import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './base.css'
import Top from './Top.tsx'
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Top />
    {/* ここでトーストのコンテナを配置します */}
    <ToastContainer
      position="top-right"
      autoClose={3000}
      hideProgressBar={false}
      closeOnClick
      pauseOnHover
      draggable
    />
  </StrictMode>
)
