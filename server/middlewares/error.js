export default (req, res, next, error) => {
    res.status(500).json(error)
}
