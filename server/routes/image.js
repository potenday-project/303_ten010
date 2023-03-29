import { Router } from 'express'
import asyncify from 'express-asyncify'

import validateJwt from '../middlewares/jwt.js'
import storage from "../middlewares/storage.js";

const router = asyncify(Router())

router.post('/', validateJwt, storage.single("photo"), (req, res) => {
    console.log(req.file)
    res.json({photoUrl: req.file.location})
})

export default router
