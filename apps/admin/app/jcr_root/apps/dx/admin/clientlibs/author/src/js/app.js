// Ignore for code coverage
/* istanbul ignore file */
import { initAuthorVh } from './utils/authorVh';
import authorWatch from './utils/authorWatch';

/* Run on document load */
authorWatch(document);
initAuthorVh(document);

// Register functions
const toWatch = [initAuthorVh];
toWatch.forEach((func) => {
    window.dx.author.watch.registerFunction(func);
});
