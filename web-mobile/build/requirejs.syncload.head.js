if (requirejs && requirejs.s && requirejs.s.contexts && requirejs.s.contexts._) {
    window.__andrea_internal_requirejs_nextTick__ = requirejs.s.contexts._.nextTick;
    requirejs.s.contexts._.nextTick = function(fn) {fn();};
}
