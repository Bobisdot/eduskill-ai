(function () {
  const select = document.querySelector(".sidebar select") ||
                 document.querySelector("#langSelect, #languageSelect, select.lang-select, #langSwitcher");
  if (!select) return;

  const CODES = { kk: "KZ", kz: "KZ", ru: "RU", en: "EN" };
  const code = v => CODES[String(v).toLowerCase()] || String(v).toUpperCase().slice(0, 2);
  const norm = v => (String(v).toLowerCase() === "kz" ? "kk" : String(v).toLowerCase());

  const sw = document.createElement("div");
  sw.className = "lang-switch";
  sw.setAttribute("role", "group");
  sw.setAttribute("aria-label", "Language");

  [...select.options].forEach(o => {
    const b = document.createElement("button");
    b.type = "button";
    b.dataset.value = o.value;
    b.textContent = code(o.value);
    b.title = o.textContent.replace(/^[^\p{L}]+/u, "").replace(/^(KZ|RU|GB|EN)\s+/, "").trim();
    b.setAttribute("aria-label", b.title);
    b.addEventListener("click", () => {
      if (select.value === o.value) return;
      select.value = o.value;
      select.dispatchEvent(new Event("change", { bubbles: true }));
      sync();
    });
    sw.appendChild(b);
  });

  function sync() {
    sw.querySelectorAll("button").forEach(b =>
      b.setAttribute("aria-pressed", String(b.dataset.value === select.value)));
    document.documentElement.lang = norm(select.value);
    if (typeof updateThemeBtn === "function") updateThemeBtn();
  }
  select.addEventListener("change", sync);

  const topbar = document.querySelector(".topbar");
  if (topbar && topbar.lastElementChild) {
    let right = topbar.lastElementChild;
    if (right.matches(".who")) {
      const w = document.createElement("div");
      topbar.appendChild(w);
      w.appendChild(right);
      right = w;
    }
    right.classList.add("topbar-right");
    right.prepend(sw);
  } else {
    sw.classList.add("floating");
    document.body.appendChild(sw);
  }

  const box = select.parentElement;
  if (box && !box.matches(".sidebar") && box.children.length === 1) box.hidden = true;
  else select.hidden = true;

  sync();
})();