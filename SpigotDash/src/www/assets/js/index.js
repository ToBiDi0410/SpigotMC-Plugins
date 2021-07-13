function refreshSections(id) {
    var secs = document.querySelectorAll(".section");
    secs.forEach((elem) => {
        if (elem.id != ("sec_" + id)) {
            elem.classList.add("section_disabled")
            elem.classList.remove("section_enabled")
            return;
        }
        elem.classList.remove("section_disabled")
        elem.classList.add("section_enabled")

    })
}

function setOffline(off) {
    if (off == true) {
        console.log("Recieved an hint, that the server is offline...");
        document.getElementById("offlinewarn").classList.add("shown");
    } else {
        document.getElementById("offlinewarn").classList.remove("shown");
    }
}

document.addEventListener('DOMContentLoaded', () => {
    refreshSections("plugins");
    // Get all "navbar-burger" elements
    const $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);

    // Check if there are any navbar burgers
    if ($navbarBurgers.length > 0) {

        // Add a click event on each of them
        $navbarBurgers.forEach(el => {
            el.addEventListener('click', () => {

                // Get the target from the "data-target" attribute
                const target = el.dataset.target;
                const $target = document.getElementById(target);

                // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
                el.classList.toggle('is-active');
                $target.classList.toggle('is-active');

            });
        });
    }

});