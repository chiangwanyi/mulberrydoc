import Vue from "vue";
import VueRouter, {RouteConfig} from "vue-router";

import Home from "../views/Home.vue";
import Authentication from "../views/Authentication.vue";
import Documents from "../views/Documents.vue";

import RichText from "../views/editor/RichText.vue";
import Markdown from "../views/editor/Markdown.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        name: 'Home',
        redirect: '/documents',
    },
    // {
    //     path: "/about",
    //     name: "About",
    //     component: () => import("../views/About.vue"),
    // },
    {
        path: '/documents*',
        name: 'Documents',
        component: Documents,
    },
    {
        path: "/auth",
        name: "Authentication",
        component: Authentication,
    },
    {
        path: "/doc*",
        name: "RichText",
        component: RichText
    },
    {
        path: "/md*",
        name: "Markdown",
        component: Markdown
    },
];

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes,
});

export default router;
