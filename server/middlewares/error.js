export default (error, req, res, next) => {
    res.status(500).json(error)
}
