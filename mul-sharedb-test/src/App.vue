<template>
    <div id="app">
        <RichText/>
    </div>
</template>

<script>
    import RichText from "./views/RichText";

    export default {
        name: "App",
        components: {
            RichText,
        },
    };
</script>

<style>
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        color: #2c3e50;
    }
</style>
