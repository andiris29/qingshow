if (window.__andrea_internal_requirejs_nextTick__ !== undefined) {
    if (requirejs && requirejs.s && requirejs.s.contexts && requirejs.s.contexts._) {
        requirejs.s.contexts._.nextTick = window.__andrea_internal_requirejs_nextTick__;
    }
    window.__andrea_internal_requirejs_nextTick__ = undefined;
}