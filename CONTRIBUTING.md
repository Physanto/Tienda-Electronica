# Cómo contribuir

## 🚀 Pasos

### 1. Actualiza tu repositorio

SIEMPRE, antes de empezar a trabajar en el proyecto por favor actualizar tu repositorio local.

```bash
git pull origin main
```

---

### 2. Crea una rama nueva (OBLIGATORIO)

Cada vez que vayas hacer algo nuevo DEBES crear rama nueva.

Usa nombres claros según lo que hagas:

```bash
git checkout -b tipo/nombre-cambio
```

#### Ejemplos de ramas:

* `fix/login-error`
* `fix/validacion-formulario`
* `feature/pantalla-registro`
* `feature/api-usuarios`
* `refactor/limpieza-codigo`
* `docs/actualizar-readme`

---

### 3. Haz tus cambios y guarda

---

### 4. Agrega y haz commit

```bash
git add .
git commit -m "tipo: breve descripción"
```

#### Ejemplos de commits:

* `fix: corrige error en login`
* `fix: valida campos vacíos`
* `feature: agrega pantalla de registro`
* `feature: conecta API de usuarios`
* `refactor: mejora estructura del código`
* `docs: actualiza instrucciones`

---

### 5. Sube tu rama (NO main)

```bash
git push origin nombre-de-tu-rama
```

---

### 6. Crea un Pull Request en GitHub

* Ve al repositorio
* Selecciona tu rama
* Click en **"Compare & pull request"**

---

## ⚠️ Reglas importantes

* ❌ NO subir archivos con credenciales (`.json`, `.env`)
* ❌ NO trabajar directo en `main`
* ✅ SIEMPRE usar ramas
* ✅ Nombres claros en ramas y commits

---

## ✅ Antes de subir

* [ ] Probé mi código
* [ ] Compilé y ejecuté el codigo
* [ ] No rompí nada
* [ ] No subí archivos sensibles
* [ ] El commit tiene un nombre claro
