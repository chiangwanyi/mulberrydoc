import Quill from "quill";

const Parchment = Quill.import("parchment");
const Delta = Quill.import("delta");

const AuthorAttribute = new Parchment.Attributor.Class('author', 'ql-author', {
    scope: Parchment.Scope.INLINE
});

Quill.register(AuthorAttribute);

class Authorship {

}

export default Authorship;