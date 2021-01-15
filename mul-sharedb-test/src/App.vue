<template>
  <div id="app">
<!--    <HelloWorld />-->
<!--    <QuillTest/>-->
      <WangEditorTest/>
  </div>
</template>

<script>
// import HelloWorld from "./components/HelloWorld.vue";
// import QuillTest from "./components/QuillTest";
import WangEditorTest from "./components/WangEditorTest";

export default {
  name: "App",
  components: {
      WangEditorTest,
    // QuillTest,
    // HelloWorld,
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
