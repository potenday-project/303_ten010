import jwt from 'jsonwebtoken'

export default (req, res, next) => {
    try {
        const token = req.header('authorization')
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = {
            email: decoded.email,
        }
        next()
    } catch (err) {
        res.status(401).json({"error": "invalid jwt"})
    }
}