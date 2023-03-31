import { Router } from 'express'
import jwt from 'jsonwebtoken'

const router = Router()

router.post('/', (req, res) => {
    if (!req.body.email) {
        res.status(400).json({error:"bad request"})
    }
    const payload = { // access token에 들어갈 payload
        email: req.body.email,
        nickname: req.body.nickname,
    };
    const secret = process.env.JWT_SECRET
    const token = jwt.sign(payload, secret);

    res.json({token: token})
})

export default router
