import { Router } from 'express'
import asyncify from 'express-asyncify'

import validateJwt from '../middlewares/jwt.js'
import Gallery from '../models/galleries.js'

const router = asyncify(Router())

router.post('/', validateJwt, async (req, res) => {
    let gallery = new Gallery()
    gallery.email = req.user.email
    gallery.photoUrl = req.body.photoUrl
    gallery.text = req.body.text
    await gallery.save()
    res.json({success: true})
})

router.get('/', validateJwt, async (req, res) => {
    let query
    if(req.query.type==="user"){
        query = {email: req.user.email}
    } else {
        query = {}
    }
    const result = await Gallery.find(query)
    res.json({result, count: result.length})
})

router.delete('/:id', validateJwt, async (req, res) => {
    await Gallery.findByIdAndDelete(req.params.id)
    res.json({success: true})
})

export default router
