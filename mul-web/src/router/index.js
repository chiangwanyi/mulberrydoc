import Vue from 'vue'
import VueRouter from 'vue-router'

import Login from "../views/Login";
import Documents from "../views/MyDocuments";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        redirect: '/documents',
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    // {
    //     path: '/documents',
    //     name: 'Documents',
    //     redirect: '/documents/*',
    // },
    {
        path: '/documents*',
        name: 'Documents',
        component: Documents,
    },
    {
        path: '/doc',
        name: 'MulDoc',
        component: () => import(/* webpackChunkName: "about" */ '../views/MulDoc.vue')
    },
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
