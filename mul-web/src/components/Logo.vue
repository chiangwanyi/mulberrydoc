<template>
    <div class="logo">
        <img src="../assets/logo.svg" alt="logo">
        <h2>桑叶文档</h2>
    </div>
</template>

<script>
    export default {
        name: "Logo"
    }
</script>

<style scoped lang="scss">
    .logo {
        display: flex;
        flex-direction: row;
        padding: 20px 20px;

        img {
            width: 36px;
            height: 36px;
            margin-right: 2px;
        }

        h2 {
            margin: 0;
            padding: 0;
        }
    }
</style>